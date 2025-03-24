package com.kcb.interview.kcb.repository;

import com.kcb.interview.kcb.model.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsLogRepository extends JpaRepository<SmsLog, Integer> {
}
