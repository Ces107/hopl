package com.hopl.controller;

import com.hopl.dto.document.GenerateDocRequestDto;
import com.hopl.dto.document.GenerateDocResponseDto;
import com.hopl.model.enums.DocumentType;
import com.hopl.security.JwtTokenProvider;
import com.hopl.service.CreditService;
import com.hopl.service.DocumentGeneratorService;
import com.hopl.service.PdfExportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentGeneratorService documentService;
    private final PdfExportService pdfExportService;
    private final CreditService creditService;
    private final JwtTokenProvider tokenProvider;

    public DocumentController(DocumentGeneratorService documentService,
                              PdfExportService pdfExportService,
                              CreditService creditService,
                              JwtTokenProvider tokenProvider) {
        this.documentService = documentService;
        this.pdfExportService = pdfExportService;
        this.creditService = creditService;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Returns all available document types.
     *
     * @return list of document types with display names
     */
    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getDocumentTypes() {
        List<Map<String, String>> types = Arrays.stream(DocumentType.values())
                .map(dt -> Map.of("value", dt.name(), "label", dt.getDisplayName()))
                .toList();
        return ResponseEntity.ok(types);
    }

    /**
     * Generates a new document using AI.
     *
     * @param request generation parameters
     * @param httpRequest for extracting user ID from JWT
     * @return generated document
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@Valid @RequestBody GenerateDocRequestDto request,
                                      HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);

        if (!creditService.canGenerate(userId)) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                    .body(Map.of("error", "No credits available. Please purchase a plan to generate documents."));
        }

        GenerateDocResponseDto doc = documentService.generate(request, userId);
        creditService.consumeCredit(userId);
        return ResponseEntity.ok(doc);
    }

    /**
     * Lists all documents for the authenticated user.
     *
     * @param httpRequest for extracting user ID
     * @return list of user's documents
     */
    @GetMapping
    public ResponseEntity<List<GenerateDocResponseDto>> listDocuments(HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return ResponseEntity.ok(documentService.getUserDocuments(userId));
    }

    /**
     * Gets a single document by ID.
     *
     * @param id document ID
     * @param httpRequest for extracting user ID
     * @return the document or 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenerateDocResponseDto> getDocument(@PathVariable Long id,
                                                               HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return documentService.getDocument(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Exports a document as PDF.
     *
     * @param id document ID
     * @param httpRequest for extracting user ID
     * @return PDF file bytes
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return documentService.getDocument(id, userId)
                .map(doc -> {
                    byte[] pdf = pdfExportService.exportToPdf(doc.getTitle(), doc.getContent(), doc.getBusinessName());
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDisposition(ContentDisposition.attachment()
                            .filename(doc.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf")
                            .build());
                    return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Long extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return tokenProvider.getUserIdFromToken(header.substring(7));
        }
        throw new RuntimeException("Authentication required");
    }
}
