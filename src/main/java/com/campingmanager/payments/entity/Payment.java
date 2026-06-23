package com.campingmanager.payments.entity;

import com.campingmanager.bikes.entity.BikeRental;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // un pagamento corrisponde a un noleggio
    @OneToOne
    @JoinColumn(name = "bike_rental_id", nullable = false)
    private BikeRental bikeRental;

    @Column(name = "stripe_session_id", unique = true)
    private String stripeSessionId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paidAt;
}
