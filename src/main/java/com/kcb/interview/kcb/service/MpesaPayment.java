package com.kcb.interview.kcb.service;

import com.kcb.interview.kcb.jms.Sender;
import com.kcb.interview.kcb.model.ClientAuth;
import com.kcb.interview.kcb.model.PaymentTransaction;
import com.kcb.interview.kcb.objects.PaymentReq;
import com.kcb.interview.kcb.repository.PaymentTransactionRepository;
import com.kcb.interview.kcb.util.GlobalMethods;
import com.kcb.interview.kcb.util.HttpProcessor;
import lombok.extern.java.Log;
import org.asynchttpclient.RequestBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.logging.Level;

@Log
@Component
@SuppressWarnings("Duplicates")
public class MpesaPayment {
    @Value(value = "${org.app.properties.gateway.mpesa.gateway.b2c_request_URL}")
    private String B2C_URL;
    @Value(value = "${org.app.properties.gateway.mpesa.gateway.b2c_queue_timeOutURL}")
    private String B2C_QUEUE_URL;
    @Value(value = "${org.app.properties.gateway.mpesa.gateway.b2c_result_URL}")
    private String B2C_RESULTS_URL;


    @Value(value = "${org.app.properties.gateway.mpesa.gateway.b2c_initiatorName}")
    private String INITIATOR_NAME;
    @Value(value = "${org.app.properties.gateway.mpesa.gateway.b2c_security_credential}")
    private String SECURITY_CREDETIALS;
    @Value(value = "${org.app.properties.gateway.mpesa.gateway.b2c_command_id}")
    private String B2C_COMMAND;
    @Value(value = "${org.app.properties.gateway.mpesa.gateway.b2c_partyA}")
    private String B2C_PARTYA;

    @Value(value = "${org.app.properties.gateway.mpesa.gateway.mpesa_authenticate_url}")
    private String URL_AUTH;

    @Value(value = "${org.app.properties.gateway.mpesa.gateway.consumer_key}")
    private String consumerKey;

    @Value(value = "${org.app.properties.gateway.mpesa.gateway.consumer_secret}")
    private String consumerSecret;
    private final Sender sender;
    private final GlobalMethods globalMethods;
    private final HttpProcessor httpProcessor;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    public MpesaPayment(Sender sender,
                        GlobalMethods globalMethods, HttpProcessor httpProcessor,
                        PaymentTransactionRepository paymentTransactionRepository) {
        this.sender = sender;
        this.globalMethods = globalMethods;
        this.httpProcessor = httpProcessor;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    public String getToken() {
        try {
            String app_key = consumerKey;
            String app_secret = consumerSecret;
            String appKeySecret = app_key + ":" + app_secret;
            byte[] bytes = appKeySecret.getBytes("ISO-8859-1");
            String encoded = Base64.getEncoder().encodeToString(bytes);

            RequestBuilder builder = new RequestBuilder("GET");
            builder.addHeader("Authorization",
                            "Basic " + encoded)
                    .setUrl(URL_AUTH)
                    .build();
            JSONObject tokenRes = httpProcessor.jsonRequestProcessor(builder);

            try {
                if (tokenRes.getString("StatusCode").equals("200")) {
                    JSONObject jsonObject = new JSONObject(tokenRes.getString("ResponseBody"));
                    return jsonObject.getString("access_token");
                } else {
                    log.log(Level.WARNING, tokenRes.toString());
                }
            } catch (JSONException err) {
                log.log(Level.SEVERE, err.getMessage());
                return null;
            }
            return "Error";
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return "Error";
    }

    public JSONObject pickAndProcess(PaymentReq paymentReq) {
        JSONObject apiResponse = new JSONObject();
        try {
            ClientAuth client = globalMethods.fetchUserDetails();
            paymentTransactionRepository.findPaymentTransactionByTransactionReference(paymentReq.getPaymentReference())
                    .ifPresentOrElse(paymentTransaction -> {
                        apiResponse.put("status", "88")
                                .put("payment_status", paymentTransaction.getStatus())
                                .put("description", "Duplicate transaction");
                    }, () -> {
                        PaymentTransaction paymentTransaction = new PaymentTransaction();
                        paymentTransaction.setClientId(client.getId());
                        paymentTransaction.setTransactionReference(paymentReq.getPaymentReference());
                        paymentTransaction.setAmount(new BigDecimal(paymentReq.getAmount()));
                        paymentTransaction.setPhoneNumber(paymentReq.getPhoneNumber());
                        paymentTransaction.setStatus(0);
                        paymentTransaction.setRequestDate(Timestamp.from(Instant.now()));

                        paymentTransactionRepository.save(paymentTransaction);
                        String accessToken = getToken();

                        if (accessToken == null || accessToken.equals("") || accessToken.contains("Error")) {
                            log.log(Level.SEVERE, "Unable to get access token");
                            paymentTransaction.setStatus(5);

                            paymentTransaction.setResponseDescription("Error In Getting Access Token");
                            paymentTransaction.setResultDate(Timestamp.from(Instant.now()));
                            paymentTransactionRepository.save(paymentTransaction);
                            apiResponse.put("status", "99")
                                    .put("description", "Service is currently down please try again later");
                        } else {
                            JSONObject requestPayload = requestPayload(paymentReq.getAmount(), paymentReq.getPhoneNumber());
                            log.log(Level.INFO, requestPayload.toString());
                            JSONObject requestResponse = sendRequest(accessToken, requestPayload);
                            log.log(Level.INFO, requestResponse.toString());
                            paymentTransaction.setResponseDate(Timestamp.from(Instant.now()));

                            if (requestResponse.getString("StatusCode").equals("200")) {
                                JSONObject responseBody = new JSONObject(requestResponse.getString("ResponseBody").trim());
                                if (responseBody.has("ConversationID")) {
                                    paymentTransaction.setStatus(1);
                                    paymentTransaction.setOriginatorConversationId(responseBody.getString("OriginatorConversationID"));
                                    paymentTransaction.setConversationId(responseBody.getString("ConversationID"));
                                    paymentTransaction.setResponseDescription(responseBody.getString("ResponseDescription"));

                                    apiResponse.put("status", "00")
                                            .put("description", "Payment is successful. The customer will receive an sms soon");
                                    JSONObject smsPayload = new JSONObject();
                                    String message = "Dear Customer, you have been send Ksh " + paymentReq.getAmount() + " by " + client.getClient() + "Ref::" + paymentReq.getPaymentReference();
                                    smsPayload.put("PhoneNumber", paymentReq.getPhoneNumber());
                                    smsPayload.put("Message", message);
                                    sender.sendSms(smsPayload);
                                } else {
                                    paymentTransaction.setStatus(5);
                                    paymentTransaction.setResponseDescription(responseBody.getString("errorMessage"));
                                    paymentTransaction.setResultCode(Integer.valueOf(requestResponse.getString("StatusCode")));
                                    paymentTransaction.setResultDesc(responseBody.getString("errorMessage"));
                                    paymentTransaction.setResultDate(Timestamp.from(Instant.now()));
                                    apiResponse.put("status", "99")
                                            .put("description", "Service is currently down please try again later");
                                }
                                paymentTransactionRepository.save(paymentTransaction);
                            } else {
                                JSONObject responseBody = new JSONObject(requestResponse.getString("ResponseBody").trim());
                                paymentTransaction.setStatus(5);
                                paymentTransaction.setResponseDescription(responseBody.getString("errorCode"));
                                paymentTransaction.setResultCode(Integer.valueOf(requestResponse.getString("StatusCode")));
                                paymentTransaction.setResultDesc(responseBody.getString("errorMessage"));
                                paymentTransaction.setResultDate(Timestamp.from(Instant.now()));
                                paymentTransactionRepository.save(paymentTransaction);
                                apiResponse.put("status", "99")
                                        .put("description", "Service is currently down please try again later");
                            }
                        }
                    });

        } catch (Exception ex) {
            log.log(Level.WARNING, "ERROR M-PESA B2C : " + ex.getMessage());
            apiResponse.put("status", "99")
                    .put("description", "Service is currently down please try again later");
        }

        return apiResponse;
    }

    private JSONObject requestPayload(String amount, String phone) {
        JSONObject map = new JSONObject();
        map.put("InitiatorName", INITIATOR_NAME);
        map.put("SecurityCredential", SECURITY_CREDETIALS);
        map.put("CommandID", B2C_COMMAND);
        map.put("Amount", amount);
        map.put("PartyA", B2C_PARTYA);
        map.put("PartyB", phone);
        map.put("Remarks", "Cashout");
        map.put("QueueTimeOutURL", B2C_QUEUE_URL);
        map.put("ResultURL", B2C_RESULTS_URL);
        map.put("Occassion", "occassion");
        return map;
    }

    private JSONObject sendRequest(String accessToken, JSONObject map) {
        JSONObject response = new JSONObject();
        try {
            RequestBuilder builder = new RequestBuilder("POST");
            builder.addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .setBody(map.toString())
                    .setUrl(B2C_URL)
                    .build();
            response = httpProcessor.jsonRequestProcessor(builder);
        } catch (Exception e) {
            response.put("StatusCode", "999");
            log.log(Level.SEVERE, e.getMessage());
        }
        return response;
    }
}
