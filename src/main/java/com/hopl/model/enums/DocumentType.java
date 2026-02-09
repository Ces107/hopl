package com.hopl.model.enums;

public enum DocumentType {
    PRIVACY_POLICY("Privacy Policy"),
    TERMS_OF_SERVICE("Terms of Service"),
    COOKIE_POLICY("Cookie Policy"),
    REFUND_POLICY("Refund & Return Policy"),
    DMCA_NOTICE("DMCA / Copyright Notice"),
    ACCEPTABLE_USE("Acceptable Use Policy"),
    DISCLAIMER("Disclaimer"),
    NDA("Non-Disclosure Agreement"),
    FREELANCE_AGREEMENT("Freelance Service Agreement"),
    SAAS_LICENSE("SaaS License Agreement"),
    CONSULTING_AGREEMENT("Consulting Agreement"),
    BUSINESS_PLAN("Business Plan Executive Summary"),
    PROPOSAL("Professional Proposal"),
    JOB_DESCRIPTION("Job Description"),
    SOP("Standard Operating Procedure");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
