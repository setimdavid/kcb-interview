package com.kcb.interview.kcb.objects;

import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class PaymentReq {
    @NotNull
    private String paymentReference;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String amount;
}
