package com.hopl.dto.scan;

import java.util.List;

public class ScanResponseDto {
    private Long id;
    private String url;
    private int score;
    private List<IssueDto> issues;
    private List<String> recommendations;
    private String jurisdiction;
    private String riskLevel;
    
    public static class IssueDto {
        private String code;
        private String title;
        private String description;
        private int severity;
        private boolean passed;
        
        public IssueDto() {}
        public IssueDto(String code, String title, String description, int severity, boolean passed) {
            this.code = code;
            this.title = title;
            this.description = description;
            this.severity = severity;
            this.passed = passed;
        }
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getSeverity() { return severity; }
        public void setSeverity(int severity) { this.severity = severity; }
        public boolean isPassed() { return passed; }
        public void setPassed(boolean passed) { this.passed = passed; }
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public List<IssueDto> getIssues() { return issues; }
    public void setIssues(List<IssueDto> issues) { this.issues = issues; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}
