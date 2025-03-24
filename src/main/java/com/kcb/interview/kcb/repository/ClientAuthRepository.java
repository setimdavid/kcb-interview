package com.kcb.interview.kcb.repository;

import com.kcb.interview.kcb.model.ClientAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAuthRepository extends JpaRepository<ClientAuth, Integer> {
    ClientAuth findClientAuthByClientKey(String clientKey);
}
