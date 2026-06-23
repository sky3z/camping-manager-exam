package com.campingmanager.payments.service;

import com.campingmanager.bikes.entity.BikeRental;
import com.campingmanager.bikes.entity.BikeRentalStatus;
import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.payments.dto.CheckoutResponse;
import com.campingmanager.payments.entity.Payment;
import com.campingmanager.payments.entity.PaymentStatus;
import com.campingmanager.payments.repository.PaymentRepository;
import com.campingmanager.users.entity.Ospite;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final BikeRentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @Value("${app.frontend.url:http://localhost:8080}")
    private String frontendUrl;

    public CheckoutResponse createCheckoutSession(Long rentalId, Ospite ospite) {
        BikeRental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Noleggio non trovato: " + rentalId));

        if (!rental.getOspite().getId().equals(ospite.getId())) {
            throw new BadRequestException("Non puoi pagare il noleggio di un altro ospite");
        }
        if (rental.getStatus() != BikeRentalStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Il noleggio non è in attesa di pagamento");
        }

        Stripe.apiKey = stripeSecretKey;

        try {
            // stripe vuole i centesimi, non i decimali
            long amountCents = rental.getTotalPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/payment/cancel")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("eur")
                                    .setUnitAmount(amountCents)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Noleggio bici " + rental.getBike().getCode())
                                            .setDescription("Dal " + rental.getStartDate() + " al " + rental.getEndDate())
                                            .build())
                                    .build())
                            .build())
                    .build();

            Session session = Session.create(params);

            // se esiste già un pagamento pendente per questo noleggio lo riuso
            Payment payment = paymentRepository.findByBikeRentalId(rentalId)
                    .orElseGet(Payment::new);
            payment.setBikeRental(rental);
            payment.setStripeSessionId(session.getId());
            payment.setAmount(rental.getTotalPrice());
            payment.setStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            return new CheckoutResponse(session.getUrl(), session.getId());

        } catch (Exception e) {
            throw new RuntimeException("Errore Stripe: " + e.getMessage());
        }
    }

    // stripe manda un evento quando il cliente ha pagato
    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new BadRequestException("Firma webhook non valida");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            try {
                // leggo il JSON grezzo perché la versione SDK non sempre deserializza i nuovi formati API
                String rawJson = event.getDataObjectDeserializer().getRawJson();
                JsonNode sessionData = new ObjectMapper().readTree(rawJson);
                String sessionId = sessionData.get("id").asText();

                paymentRepository.findByStripeSessionId(sessionId).ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.PAID);
                    payment.setPaidAt(LocalDateTime.now());

                    // noleggio passa da PENDING_PAYMENT ad ATTIVO
                    BikeRental rental = payment.getBikeRental();
                    rental.setStatus(BikeRentalStatus.ATTIVO);
                    rentalRepository.save(rental);

                    paymentRepository.save(payment);
                });
            } catch (Exception e) {
                throw new RuntimeException("Errore nell'elaborazione del webhook: " + e.getMessage());
            }
        }
    }
}
