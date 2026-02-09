package com.hopl.model.enums;

public enum ComplianceIssue {
    MISSING_PRIVACY_POLICY("Missing Privacy Policy", "Your website does not have a Privacy Policy page. This is required by GDPR, CCPA, and most data protection laws.", 15),
    MISSING_TERMS("Missing Terms of Service", "No Terms of Service or Terms and Conditions page was found.", 10),
    MISSING_COOKIE_CONSENT("Missing Cookie Consent Banner", "No cookie consent mechanism was detected. GDPR requires explicit consent before setting non-essential cookies.", 15),
    NO_CONTACT_INFO("No Contact Information", "No visible contact email, form, or address was found. Most regulations require a way for users to contact you.", 8),
    THIRD_PARTY_COOKIES("Third-Party Tracking Without Disclosure", "Third-party scripts (analytics, ads, pixels) were detected but not disclosed in a cookie or privacy policy.", 12),
    NO_HTTPS("Not Using HTTPS", "Your website is not served over HTTPS. Data transmitted without encryption is a security risk.", 10),
    BROKEN_PRIVACY_LINK("Broken Privacy Policy Link", "A link to the privacy policy was found but returns an error.", 10),
    BROKEN_TERMS_LINK("Broken Terms Link", "A link to terms of service was found but returns an error.", 8),
    NO_DATA_COLLECTION_DISCLOSURE("No Data Collection Disclosure", "Forms collecting user data were found but no disclosure about what data is collected.", 10),
    NO_OPT_OUT("No Opt-Out Mechanism", "No unsubscribe link or opt-out mechanism was found for marketing communications.", 7),
    MISSING_COOKIE_POLICY("Missing Cookie Policy", "Cookies are being set but no separate cookie policy was found.", 8),
    NO_ACCESSIBILITY_BASICS("Missing Basic Accessibility", "Basic accessibility features (alt text, ARIA labels) are missing from key elements.", 5);

    private final String title;
    private final String description;
    private final int severity;

    ComplianceIssue(String title, String description, int severity) {
        this.title = title;
        this.description = description;
        this.severity = severity;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getSeverity() { return severity; }
}
