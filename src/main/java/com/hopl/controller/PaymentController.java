package com.hopl.controller;

import com.hopl.dto.payment.CheckoutRequest;
import com.hopl.security.JwtTokenProvider;
import com.hopl.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final JwtTokenProvider tokenProvider;

    @Value("${hopl.stripe.webhook-secret:}")
    private String webhookSecret;

    public PaymentController(PaymentService paymentService, JwtTokenProvider tokenProvider) {
        this.paymentService = paymentService;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Creates a Stripe checkout session for the specified plan.
     *
     * @param request checkout details
     * @param httpRequest for extracting user ID
     * @return checkout URL
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createCheckout(@Valid @RequestBody CheckoutRequest request,
                                                               HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        String successUrl = request.getSuccessUrl() != null ? request.getSuccessUrl() : "http://localhost:8080/dashboard";
        String cancelUrl = request.getCancelUrl() != null ? request.getCancelUrl() : "http://localhost:8080/pricing";

        String checkoutUrl = paymentService.createCheckoutSession(
                request.getPlanType(), userId, successUrl, cancelUrl);
        return ResponseEntity.ok(Map.of("url", checkoutUrl));
    }

    /**
     * Receives Stripe webhook events.
     *
     * @param payload raw webhook payload
     * @param sigHeader Stripe signature header
     * @return 200 OK on success
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload,
                                          @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event;
            if (webhookSecret != null && !webhookSecret.isBlank()) {
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } else {
                log.warn("Stripe webhook secret not configured, skipping signature verification");
                return ResponseEntity.ok("OK");
            }

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                if (session != null) {
                    paymentService.handleCheckoutCompleted(session);
                }
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Webhook processing failed", e);
            return ResponseEntity.badRequest().body("Webhook error");
        }
    }

    private Long extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return tokenProvider.getUserIdFromToken(header.substring(7));
        }
        throw new RuntimeException("Authentication required");
    }
}
