package com.kcb.interview.kcb.repository;

import com.kcb.interview.kcb.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
    Optional<PaymentTransaction> findPaymentTransactionByTransactionReference(String transactionRef);

    Optional<PaymentTransaction> findPaymentTransactionByTransactionReferenceAndClientId(String transactionRef, Integer clientId);

    List<PaymentTransaction> findPaymentTransactionByClientIdOrderByRequestDate(Integer client);
}
