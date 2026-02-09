package com.hopl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopl.dto.scan.ScanResponseDto;
import com.hopl.model.ScanResult;
import com.hopl.repository.ScanResultRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ScannerService {

    private static final Logger log = LoggerFactory.getLogger(ScannerService.class);
    private final ScanResultRepository scanResultRepository;
    private final ComplianceAnalyzer complianceAnalyzer;
    private final ObjectMapper objectMapper;

    @Value("${hopl.scan.cache-ttl-hours:24}")
    private int cacheTtlHours;

    @Value("${hopl.scan.timeout-seconds:15}")
    private int timeoutSeconds;

    public ScannerService(ScanResultRepository scanResultRepository,
                          ComplianceAnalyzer complianceAnalyzer,
                          ObjectMapper objectMapper) {
        this.scanResultRepository = scanResultRepository;
        this.complianceAnalyzer = complianceAnalyzer;
        this.objectMapper = objectMapper;
    }

    /**
     * Scans a URL for compliance issues. Returns cached result if available.
     *
     * @param url the website URL to scan
     * @param userId optional user ID for tracking
     * @return scan response with score and issues
     */
    public ScanResponseDto scan(String url, Long userId) {
        String normalizedUrl = normalizeUrl(url);
        LocalDateTime cacheThreshold = LocalDateTime.now().minusHours(cacheTtlHours);
        Optional<ScanResult> cached = scanResultRepository
                .findFirstByUrlAndCreatedAtAfterOrderByCreatedAtDesc(normalizedUrl, cacheThreshold);
        if (cached.isPresent()) {
            return toResponse(cached.get());
        }
        try {
            Document doc = Jsoup.connect(normalizedUrl)
                    .userAgent("Mozilla/5.0 (compatible; HOPL Compliance Scanner/1.0)")
                    .timeout(timeoutSeconds * 1000)
                    .followRedirects(true)
                    .get();
            ComplianceAnalyzer.AnalysisResult analysis = complianceAnalyzer.analyze(doc, normalizedUrl);
            ScanResult result = new ScanResult();
            result.setUrl(normalizedUrl);
            result.setScore(analysis.getScore());
            result.setIssuesJson(objectMapper.writeValueAsString(analysis.getIssues()));
            result.setDetailsJson(objectMapper.writeValueAsString(analysis.getDetails()));
            result.setJurisdiction(analysis.getJurisdiction());
            result.setUserId(userId);
            ScanResult saved = scanResultRepository.save(result);
            return toResponse(saved);
        } catch (Exception e) {
            log.error("Failed to scan URL: {}", normalizedUrl, e);
            throw new RuntimeException("Failed to scan website: " + e.getMessage());
        }
    }

    private String normalizeUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        try {
            URI uri = new URI(url);
            return uri.toString().replaceAll("/+$", "");
        } catch (Exception e) {
            return url;
        }
    }

    private ScanResponseDto toResponse(ScanResult result) {
        try {
            ScanResponseDto dto = new ScanResponseDto();
            dto.setId(result.getId());
            dto.setUrl(result.getUrl());
            dto.setScore(result.getScore());
            dto.setJurisdiction(result.getJurisdiction());

            List<ScanResponseDto.IssueDto> issues = objectMapper.readValue(
                    result.getIssuesJson(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ScanResponseDto.IssueDto.class));
            dto.setIssues(issues);

            // Generate recommendations based on failed checks
            List<String> recs = new ArrayList<>();
            issues.stream().filter(i -> !i.isPassed()).forEach(i -> {
                switch (i.getCode()) {
                    case "MISSING_PRIVACY_POLICY" -> recs.add("Generate a Privacy Policy tailored to your website");
                    case "MISSING_TERMS" -> recs.add("Create Terms of Service to protect your business");
                    case "MISSING_COOKIE_CONSENT" -> recs.add("Add a Cookie Consent banner and Cookie Policy");
                    case "NO_CONTACT_INFO" -> recs.add("Add visible contact information to your website");
                    case "THIRD_PARTY_COOKIES" -> recs.add("Disclose third-party tracking in your Privacy Policy");
                    case "NO_HTTPS" -> recs.add("Enable HTTPS/SSL for your website");
                    default -> recs.add("Address: " + i.getTitle());
                }
            });
            dto.setRecommendations(recs);

            if (result.getScore() >= 80) dto.setRiskLevel("LOW");
            else if (result.getScore() >= 50) dto.setRiskLevel("MEDIUM");
            else dto.setRiskLevel("HIGH");

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse scan result", e);
        }
    }
}
