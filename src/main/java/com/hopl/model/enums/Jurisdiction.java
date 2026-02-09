package com.hopl.model.enums;

public enum Jurisdiction {
    EU_GDPR("European Union - GDPR"),
    US_CCPA("United States - CCPA"),
    BR_LGPD("Brazil - LGPD"),
    CA_PIPEDA("Canada - PIPEDA"),
    UK_DPA("United Kingdom - UK DPA"),
    AU_PRIVACY("Australia - Privacy Act"),
    GLOBAL("Global / Multi-jurisdictional");

    private final String displayName;

    Jurisdiction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
