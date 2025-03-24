package com.kcb.interview.kcb.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tb_payment_transaction")
public class PaymentTransaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic
    @Column(name = "client_id")
    private Integer clientId;
    @Basic
    @Column(name = "transaction_reference")
    private String transactionReference;
    @Basic
    @Column(name = "phone_number")
    private String phoneNumber;
    @Basic
    @Column(name = "amount")
    private BigDecimal amount;
    @Basic
    @Column(name = "request_date")
    private Timestamp requestDate;
    @Basic
    @Column(name = "status")
    private Integer status;
    @Basic
    @Column(name = "request_payload")
    private String requestPayload;
    @Basic
    @Column(name = "conversation_id")
    private String conversationId;
    @Basic
    @Column(name = "originator_conversation_id")
    private String originatorConversationId;
    @Basic
    @Column(name = "response_description")
    private String responseDescription;
    @Basic
    @Column(name = "response_body")
    private String responseBody;
    @Basic
    @Column(name = "response_date")
    private Timestamp responseDate;
    @Basic
    @Column(name = "results_payload")
    private String resultsPayload;
    @Basic
    @Column(name = "result_code")
    private Integer resultCode;
    @Basic
    @Column(name = "result_desc")
    private String resultDesc;
    @Basic
    @Column(name = "transaction_id")
    private String transactionId;
    @Basic
    @Column(name = "transaction_receipt")
    private String transactionReceipt;
    @Basic
    @Column(name = "is_registered_customer")
    private String isRegisteredCustomer;
    @Basic
    @Column(name = "charges_paid")
    private BigDecimal chargesPaid;
    @Basic
    @Column(name = "receiver_name")
    private String receiverName;
    @Basic
    @Column(name = "transaction_date")
    private String transactionDate;
    @Basic
    @Column(name = "utility_account_funds")
    private BigDecimal utilityAccountFunds;
    @Basic
    @Column(name = "working_account_funds")
    private BigDecimal workingAccountFunds;
    @Basic
    @Column(name = "result_date")
    private Timestamp resultDate;
}
