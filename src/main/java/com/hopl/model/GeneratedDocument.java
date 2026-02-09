package com.hopl.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "generated_documents")
public class GeneratedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "CLOB")
    private String content;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "business_type")
    private String businessType;

    private String jurisdiction;

    @Column(length = 10)
    private String language = "en";

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "scan_id")
    private Long scanId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

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
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getScanId() { return scanId; }
    public void setScanId(Long scanId) { this.scanId = scanId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
