package com.hopl.dto.payment;

import jakarta.validation.constraints.NotBlank;

public class CheckoutRequest {
    @NotBlank
    private String planType;
    private String successUrl;
    private String cancelUrl;
    
    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }
    public String getSuccessUrl() { return successUrl; }
    public void setSuccessUrl(String successUrl) { this.successUrl = successUrl; }
    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
}
