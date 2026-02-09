package com.hopl.dto.document;

import jakarta.validation.constraints.NotBlank;

public class GenerateDocRequestDto {
    @NotBlank
    private String documentType;
    @NotBlank
    private String businessName;
    private String businessType;
    private String websiteUrl;
    private String jurisdiction;
    private String language;
    private Long scanId;
    private String additionalInfo;
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Long getScanId() { return scanId; }
    public void setScanId(Long scanId) { this.scanId = scanId; }
    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
}
