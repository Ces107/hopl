package com.hopl.repository;

import com.hopl.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByStripeSessionId(String stripeSessionId);
    java.util.List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
}
