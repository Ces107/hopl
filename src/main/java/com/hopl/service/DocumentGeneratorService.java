package com.hopl.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopl.dto.document.GenerateDocRequestDto;
import com.hopl.dto.document.GenerateDocResponseDto;
import com.hopl.model.GeneratedDocument;
import com.hopl.model.enums.DocumentType;
import com.hopl.repository.GeneratedDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class DocumentGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(DocumentGeneratorService.class);
    private final GeneratedDocumentRepository documentRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${hopl.openai.api-key:demo}")
    private String apiKey;

    @Value("${hopl.openai.model:gpt-4o-mini}")
    private String model;

    @Value("${hopl.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    public DocumentGeneratorService(GeneratedDocumentRepository documentRepository,
                                    ObjectMapper objectMapper) {
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Generates a legal/business document using AI.
     *
     * @param request document generation parameters
     * @param userId the requesting user's ID
     * @return generated document response
     */
    public GenerateDocResponseDto generate(GenerateDocRequestDto request, Long userId) {
        DocumentType docType = DocumentType.valueOf(request.getDocumentType());
        String prompt = buildPrompt(docType, request);

        String content;
        if ("demo".equals(apiKey)) {
            content = getDemoContent(docType, request.getBusinessName());
        } else {
            content = callOpenAi(prompt);
        }

        GeneratedDocument doc = new GeneratedDocument();
        doc.setDocumentType(docType.name());
        doc.setTitle(docType.getDisplayName() + " - " + request.getBusinessName());
        doc.setContent(content);
        doc.setBusinessName(request.getBusinessName());
        doc.setBusinessType(request.getBusinessType());
        doc.setJurisdiction(Optional.ofNullable(request.getJurisdiction()).orElse("GLOBAL"));
        doc.setLanguage(Optional.ofNullable(request.getLanguage()).orElse("en"));
        doc.setUserId(userId);
        doc.setScanId(request.getScanId());

        GeneratedDocument saved = documentRepository.save(doc);
        return toResponse(saved);
    }

    private String buildPrompt(DocumentType docType, GenerateDocRequestDto request) {
        String template = loadPromptTemplate(docType);
        String date = LocalDate.now().toString();
        String jurisdiction = Optional.ofNullable(request.getJurisdiction()).orElse("GLOBAL");

        return template
                .replace("{{businessName}}", request.getBusinessName())
                .replace("{{businessType}}", Optional.ofNullable(request.getBusinessType()).orElse("online business"))
                .replace("{{websiteUrl}}", Optional.ofNullable(request.getWebsiteUrl()).orElse(""))
                .replace("{{jurisdiction}}", jurisdiction)
                .replace("{{date}}", date)
                .replace("{{language}}", Optional.ofNullable(request.getLanguage()).orElse("English"))
                .replace("{{additionalInfo}}", Optional.ofNullable(request.getAdditionalInfo()).orElse(""));
    }

    private String loadPromptTemplate(DocumentType docType) {
        String filename = "prompts/" + docType.name().toLowerCase().replace("_", "-") + ".txt";
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Prompt template not found: {}. Using default.", filename);
            return getDefaultPrompt(docType);
        }
    }

    private String getDefaultPrompt(DocumentType docType) {
        return """
                You are an expert legal document writer. Generate a professional %s for the following business:
                
                Business Name: {{businessName}}
                Business Type: {{businessType}}
                Website: {{websiteUrl}}
                Jurisdiction: {{jurisdiction}}
                Date: {{date}}
                Additional info: {{additionalInfo}}
                
                Write the document in {{language}}. Use proper legal formatting with numbered sections and subsections.
                Include all standard clauses required by applicable regulations.
                The document should be comprehensive, professional, and ready to use.
                Do NOT include any AI disclaimers or notes - output ONLY the document content.
                """.formatted(docType.getDisplayName());
    }

    private String callOpenAi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You are an expert legal and business document writer. Generate professional, comprehensive documents ready for immediate use. Output ONLY the document content with proper formatting using Markdown."),
                Map.of("role", "user", "content", prompt)
        ));
        body.put("temperature", 0.3);
        body.put("max_tokens", 4000);

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/chat/completions", HttpMethod.POST, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").path(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("OpenAI API call failed", e);
            throw new RuntimeException("Failed to generate document. Please try again.");
        }
    }

    private String getDemoContent(DocumentType docType, String businessName) {
        return """
                # %s
                
                **%s**
                
                *Effective Date: %s*
                
                ---
                
                > ⚠️ DEMO MODE: This is a sample document. Configure your OPENAI_API_KEY environment variable to generate real, customized documents.
                
                ## 1. Introduction
                
                This %s ("Document") governs the relationship between %s ("Company", "we", "us") and you ("User", "you").
                
                ## 2. Sample Section
                
                This is a demonstration of the document format. The actual AI-generated document will be fully customized to your business, jurisdiction, and specific requirements.
                
                ## 3. Your Rights
                
                In the full version, this section will detail user rights specific to applicable regulations (GDPR, CCPA, LGPD, etc.).
                
                ## 4. Contact
                
                For questions about this document, contact %s.
                
                ---
                
                *This document was generated by HOPL - AI Document Generator (DEMO MODE)*
                """.formatted(docType.getDisplayName(), businessName, LocalDate.now(),
                docType.getDisplayName().toLowerCase(), businessName, businessName);
    }

    /** Retrieves all documents for a user. */
    public List<GenerateDocResponseDto> getUserDocuments(Long userId) {
        return documentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    /** Retrieves a single document by ID, verifying ownership. */
    public Optional<GenerateDocResponseDto> getDocument(Long docId, Long userId) {
        return documentRepository.findById(docId)
                .filter(d -> d.getUserId().equals(userId))
                .map(this::toResponse);
    }

    private GenerateDocResponseDto toResponse(GeneratedDocument doc) {
        GenerateDocResponseDto dto = new GenerateDocResponseDto();
        dto.setId(doc.getId());
        dto.setDocumentType(doc.getDocumentType());
        dto.setTitle(doc.getTitle());
        dto.setContent(doc.getContent());
        dto.setBusinessName(doc.getBusinessName());
        dto.setJurisdiction(doc.getJurisdiction());
        dto.setCreatedAt(doc.getCreatedAt());
        return dto;
    }
}
