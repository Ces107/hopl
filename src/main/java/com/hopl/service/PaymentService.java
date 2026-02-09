package com.hopl.service;

import com.hopl.exception.ApiException;
import com.hopl.model.Payment;
import com.hopl.model.enums.PlanType;
import com.hopl.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final CreditService creditService;

    @Value("${hopl.stripe.secret-key:}")
    private String stripeSecretKey;

    private static final Map<String, Long> PLAN_PRICES = Map.of(
            "QUICK_FIX", 499L,
            "FULL_COMPLIANCE", 2999L,
            "ANNUAL_GUARD", 4999L,
            "PRO", 1999L,
            "AGENCY", 4999L
    );

    private static final Map<String, String> PLAN_NAMES = Map.of(
            "QUICK_FIX", "Quick Fix - 1 Document",
            "FULL_COMPLIANCE", "Full Compliance - All Documents",
            "ANNUAL_GUARD", "Annual Guard - Full + Updates",
            "PRO", "Pro Monthly - Unlimited",
            "AGENCY", "Agency - White Label + API"
    );

    public PaymentService(PaymentRepository paymentRepository, CreditService creditService) {
        this.paymentRepository = paymentRepository;
        this.creditService = creditService;
    }

    /**
     * Creates a Stripe Checkout session for the given plan.
     *
     * @param planType plan to purchase
     * @param userId the purchasing user's ID
     * @param successUrl redirect URL after successful payment
     * @param cancelUrl redirect URL after cancelled payment
     * @return Stripe checkout session URL
     */
    public String createCheckoutSession(String planType, Long userId, String successUrl, String cancelUrl) {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new ApiException("Payment system not configured. Set STRIPE_SECRET_KEY.", HttpStatus.SERVICE_UNAVAILABLE);
        }

        Long priceInCents = PLAN_PRICES.get(planType);
        String planName = PLAN_NAMES.get(planType);
        if (priceInCents == null || planName == null) {
            throw new ApiException("Invalid plan: " + planType, HttpStatus.BAD_REQUEST);
        }

        boolean isSubscription = "PRO".equals(planType) || "AGENCY".equals(planType);

        try {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(isSubscription ? SessionCreateParams.Mode.SUBSCRIPTION : SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .putMetadata("userId", userId.toString())
                    .putMetadata("planType", planType);

            if (isSubscription) {
                builder.addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(priceInCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(planName)
                                        .build())
                                .setRecurring(SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                        .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                                        .build())
                                .build())
                        .setQuantity(1L)
                        .build());
            } else {
                builder.addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(priceInCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(planName)
                                        .build())
                                .build())
                        .setQuantity(1L)
                        .build());
            }

            Session session = Session.create(builder.build());

            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setAmountCents(priceInCents.intValue());
            payment.setCurrency("EUR");
            payment.setStatus("PENDING");
            payment.setPaymentType(planType);
            payment.setStripeSessionId(session.getId());
            paymentRepository.save(payment);

            return session.getUrl();
        } catch (StripeException e) {
            log.error("Stripe error creating checkout session", e);
            throw new ApiException("Payment processing error: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Handles a completed Stripe checkout session.
     *
     * @param session the completed Stripe session
     */
    public void handleCheckoutCompleted(Session session) {
        String sessionId = session.getId();
        paymentRepository.findByStripeSessionId(sessionId).ifPresent(payment -> {
            payment.setStatus("COMPLETED");
            payment.setStripePaymentIntent(session.getPaymentIntent());
            paymentRepository.save(payment);

            PlanType planType = PlanType.valueOf(payment.getPaymentType());
            creditService.grantCredits(payment.getUserId(), planType);
            log.info("Payment completed for user {} plan {}", payment.getUserId(), planType);
        });
    }
}
