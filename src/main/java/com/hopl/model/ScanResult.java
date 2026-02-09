package com.hopl.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan_results")
public class ScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "issues_json", nullable = false, columnDefinition = "CLOB")
    private String issuesJson;

    @Column(name = "details_json", columnDefinition = "CLOB")
    private String detailsJson;

    private String jurisdiction;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getIssuesJson() { return issuesJson; }
    public void setIssuesJson(String issuesJson) { this.issuesJson = issuesJson; }
    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }
    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
