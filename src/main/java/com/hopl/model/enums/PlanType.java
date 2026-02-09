package com.hopl.model.enums;

public enum PlanType {
    FREE(0),
    QUICK_FIX(1),
    FULL_COMPLIANCE(15),
    ANNUAL_GUARD(15),
    PRO(-1),
    AGENCY(-1);

    private final int documentLimit;

    PlanType(int documentLimit) {
        this.documentLimit = documentLimit;
    }

    /** Returns max documents per purchase/period. -1 means unlimited. */
    public int getDocumentLimit() { return documentLimit; }

    public boolean isUnlimited() { return documentLimit == -1; }
}
