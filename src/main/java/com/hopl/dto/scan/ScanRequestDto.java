package com.hopl.dto.scan;

import jakarta.validation.constraints.NotBlank;

public class ScanRequestDto {
    @NotBlank(message = "URL is required")
    private String url;
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
