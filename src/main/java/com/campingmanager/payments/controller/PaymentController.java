package com.campingmanager.payments.controller;

import com.campingmanager.payments.dto.CheckoutResponse;
import com.campingmanager.payments.service.PaymentService;
import com.campingmanager.users.entity.Ospite;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    // l'ospite avvia il pagamento per il suo noleggio
    @PostMapping("/checkout/{rentalId}")
    @PreAuthorize("hasRole('OSPITE')")
    public ResponseEntity<CheckoutResponse> checkout(@PathVariable Long rentalId,
                                                     @AuthenticationPrincipal Ospite ospite) {
        return ResponseEntity.ok(service.createCheckoutSession(rentalId, ospite));
    }

    // stripe chiama questo dopo che l'utente ha pagato — niente JWT
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload,
                                        @RequestHeader("Stripe-Signature") String sigHeader) {
        service.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}
