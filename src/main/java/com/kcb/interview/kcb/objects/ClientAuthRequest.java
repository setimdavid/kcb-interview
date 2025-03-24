package com.kcb.interview.kcb.objects;

import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class ClientAuthRequest {

    @NotNull
    private String clientKey;
    @NotNull
    private String clientSecret;
}
