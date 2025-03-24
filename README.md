# KCB Interview Service

A lightweight Spring Boot REST API service for authentication and transaction operations.

## Endpoints

1. **Access Token**
   - **POST /api/v1/token**
   - Request body: `{
            "clientKey": "kcb_key",
            "clientSecret": "qwerty1234"
        }`
   - Response: Returns an `access_token`, `refresh_token`, and client info.

2. **Refresh Token**
   - **POST /api/v1/refresh-token**
   - Request body: `{ "refreshToken": "..." }`
   - Response: Returns a new `accessToken` and `refreshToken`.

3. **Payments**
   - **POST /api/v1/payments/payment**
   - Request body: 

   {
        "paymentReference": "2025032411141",
        "phoneNumber": "254722680308",
        "amount": "100"
    }

   - Response: Processes a payment or returns payment details.

4. **Status**
   - **GET /api/v1/payments/status**
   - Param contains transactionref eg /api/v1/payments/status/2025032411141
   - Response: Returns the service status (e.g., UP, version, timestamp).

5. **Repost**
   - **GET /api/v1/payments/report**
   - Response: Pulls list of transactions


### Using Maven
1. Build:
   ```bash
   mvn clean package


   java -jar target/kcb-payment-service.jar