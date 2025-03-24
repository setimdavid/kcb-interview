package com.kcb.interview.kcb.jms;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.ConcurrentHashMap;

@Log
@SuppressWarnings("Duplicates")
public class Sender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendSms(JSONObject messagePayload) {
        jmsTemplate.convertAndSend("sms.notification.q", messagePayload.toString());
    }

}