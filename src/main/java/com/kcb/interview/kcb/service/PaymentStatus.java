package com.kcb.interview.kcb.service;

import com.kcb.interview.kcb.model.ClientAuth;
import com.kcb.interview.kcb.repository.PaymentTransactionRepository;
import com.kcb.interview.kcb.util.GlobalMethods;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log
@Component
@SuppressWarnings("Duplicates")
public class PaymentStatus {
    private final GlobalMethods globalMethods;
    private final PaymentTransactionRepository paymentTransactionRepository;

    public PaymentStatus(GlobalMethods globalMethods,
                         PaymentTransactionRepository paymentTransactionRepository) {
        this.globalMethods = globalMethods;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    public JSONObject paymentStatus(String ref){
        JSONObject apiResponse = new JSONObject();
        ClientAuth client = globalMethods.fetchUserDetails();
        paymentTransactionRepository.findPaymentTransactionByTransactionReferenceAndClientId(ref, client.getId())
                .ifPresentOrElse(paymentTransaction -> {
                    apiResponse.put("status","00")
                            .put("message", "Transaction exists")
                            .put("amount",paymentTransaction.getAmount())
                            .put("payment_status", paymentTransaction.getStatus())
                            .put("payment_date",paymentTransaction.getRequestDate())
                            .put("payment_transaction", paymentTransaction.getTransactionReference())
                            .put("phone_number", paymentTransaction.getPhoneNumber());
                },()->{
                    apiResponse.put("status", "99")
                            .put("description", "Transaction does not exist");
                });
        return apiResponse;
    }

    public JSONObject paymentList(){
        JSONObject apiResponse = new JSONObject();
        List<JSONObject> paymentDetails = new ArrayList<>();
        ClientAuth client = globalMethods.fetchUserDetails();
        paymentTransactionRepository.findPaymentTransactionByClientIdOrderByRequestDate(client.getId())
                .forEach(paymentTransaction -> {
                    JSONObject payment = new JSONObject();
                    payment.put("status","00")
                            .put("message", "Transaction exists")
                            .put("amount",paymentTransaction.getAmount())
                            .put("payment_status", paymentTransaction.getStatus())
                            .put("payment_date",paymentTransaction.getRequestDate())
                            .put("payment_transaction", paymentTransaction.getTransactionReference())
                            .put("phone_number", paymentTransaction.getPhoneNumber());
                    paymentDetails.add(payment);
                });
        apiResponse.put("data", paymentDetails);
        return apiResponse;
    }
}
