package com.kcb.interview.kcb.controller;


import com.kcb.interview.kcb.objects.PaymentReq;
import com.kcb.interview.kcb.service.MpesaPayment;
import com.kcb.interview.kcb.service.PaymentStatus;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final MpesaPayment mpesaPayment;
    private final PaymentStatus paymentStatus;

    public PaymentController(MpesaPayment mpesaPayment,
                             PaymentStatus paymentStatus) {
        this.mpesaPayment = mpesaPayment;
        this.paymentStatus = paymentStatus;
    }

    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public ResponseEntity<?> payment(@RequestBody PaymentReq paymentReq) {
        JSONObject responseMap = mpesaPayment.pickAndProcess(paymentReq);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap.toString());
    }

    @RequestMapping(value = {"/status/{paymentReq}"}, method = RequestMethod.GET)
    public ResponseEntity<?> paymentStatus(@PathVariable String paymentReq) {
        JSONObject responseMap = paymentStatus.paymentStatus(paymentReq);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap.toString());
    }

    @RequestMapping(value = {"/report"}, method = RequestMethod.GET)
    public ResponseEntity<?> paymentList() {
        JSONObject responseMap = paymentStatus.paymentList();
        return ResponseEntity.status(HttpStatus.OK).body(responseMap.toString());
    }
}
