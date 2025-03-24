package com.kcb.interview.kcb.jms;
import com.kcb.interview.kcb.model.SmsLog;
import com.kcb.interview.kcb.repository.SmsLogRepository;
import com.kcb.interview.kcb.util.HttpProcessor;
import lombok.extern.java.Log;
import org.asynchttpclient.RequestBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.logging.Level;


@Log
public class Receiver {

    @Value(value = "${org.kcb.sms.end.point}")
    private String SMS_END_POINT;

    private final HttpProcessor httpProcessor;
    private final SmsLogRepository smsLogRepository;

    public Receiver(HttpProcessor httpProcessor,
                    SmsLogRepository smsLogRepository) {
        this.httpProcessor = httpProcessor;
        this.smsLogRepository = smsLogRepository;
    }

    @JmsListener(destination = "sms.notification.q")
    public void receive(String message) {
        sendSMS(new JSONObject(message));
    }

    public void sendSMS(JSONObject reqBody) {
        try {
            SmsLog slog = new SmsLog();
            slog.setMessage(reqBody.getString("Message"));
            slog.setPhoneNumber(reqBody.getString("PhoneNumber"));
            slog.setCreatedDate(Timestamp.from(Instant.now()));
            smsLogRepository.save(slog);
            sendHttpSMS(reqBody);
            
        } catch (Exception ex) {
            log.log(Level.WARNING, ex.getMessage());
        }
    }

    private JSONObject sendHttpSMS( JSONObject map) {
        JSONObject response = new JSONObject();
        try {
            RequestBuilder builder = new RequestBuilder("POST");
            builder.addHeader("Content-Type", "application/json")
                    .setBody(map.toString())
                    .setUrl(SMS_END_POINT)
                    .build();
            response = httpProcessor.jsonRequestProcessor(builder);
        } catch (Exception e) {
            response.put("StatusCode", "999");
            log.log(Level.SEVERE, e.getMessage());
        }
        return response;
    }

}