-- tb_client_auth table
CREATE TABLE tb_client_auth (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client VARCHAR(50) NOT NULL,
    client_key VARCHAR(255) NOT NULL,
    client_secret VARCHAR(255) NOT NULL,
    created_date TIMESTAMP,
    update_date TIMESTAMP
);

-- tb_refresh_token table
CREATE TABLE tb_refresh_token (
      id INT AUTO_INCREMENT PRIMARY KEY,
      client_key INT,
      token VARCHAR(255),
      expiry_date TIMESTAMP,
      created_date TIMESTAMP,
      CONSTRAINT fk_refresh_token_client FOREIGN KEY (client_key) REFERENCES tb_client_auth(id)
);

-- tb_payment_transaction table
CREATE TABLE tb_payment_transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(255),
    transaction_reference VARCHAR(255),
    phone_number VARCHAR(255),
    amount DECIMAL(19,4),
    request_date TIMESTAMP,
    status INT,
    request_payload TEXT,
    conversation_id VARCHAR(255),
    originator_conversation_id VARCHAR(255),
    response_description TEXT,
    response_body TEXT,
    response_date TIMESTAMP,
    results_payload TEXT,
    result_code INT,
    result_desc VARCHAR(255),
    transaction_id VARCHAR(255),
    transaction_receipt VARCHAR(255),
    is_registered_customer VARCHAR(255),
    charges_paid DECIMAL(19,4),
    receiver_name VARCHAR(255),
    transaction_date VARCHAR(255),
    utility_account_funds DECIMAL(19,4),
    working_account_funds DECIMAL(19,4),
    result_date TIMESTAMP
);

-- tb_sms table
CREATE TABLE tb_sms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(255),
    message TEXT,
    created_date TIMESTAMP
);
-- default client - secret - qwerty1234
INSERT INTO tb_client_auth (client, client_key, client_secret, created_date, update_date)
VALUES ('KCB Payments', 'kcb_key', '$2a$10$wf/jFRCMx/x.kW7TB7.dOukFQZVDo93NNRNSCsrFNrR4Kz2jJGuga', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);