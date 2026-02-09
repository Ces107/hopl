package com.hopl.dto.document;

import java.time.LocalDateTime;

public class GenerateDocResponseDto {
    private Long id;
    private String documentType;
    private String title;
    private String content;
    private String businessName;
    private String jurisdiction;
    private LocalDateTime createdAt;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
