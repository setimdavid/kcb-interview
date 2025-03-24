package com.kcb.interview.kcb.config;

public class EndPointConstant {
    public static String versioning = "/api/v1";

    public static String[] whitelistEndpoints = {
            /*
               DATABASE
            */
            "/h2-console",
            /*
            OPEN AUTH SERVICES
             */
            versioning + "/token",
            /*
            EXTERNAL APIS
             */
            versioning + "/external/mpesa/b2c/response",

    };
}
