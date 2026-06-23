package com.campingmanager.payments.repository;

import com.campingmanager.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByStripeSessionId(String sessionId);

    Optional<Payment> findByBikeRentalId(Long bikeRentalId);
}
