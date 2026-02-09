package com.hopl.controller;

import com.hopl.dto.scan.ScanRequestDto;
import com.hopl.dto.scan.ScanResponseDto;
import com.hopl.service.ScannerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scan")
public class ScanController {

    private final ScannerService scannerService;

    public ScanController(ScannerService scannerService) {
        this.scannerService = scannerService;
    }

    /**
     * Scans a website URL for compliance issues. Public endpoint - no auth required.
     *
     * @param request contains the URL to scan
     * @return compliance scan results with score and issues
     */
    @PostMapping
    public ResponseEntity<ScanResponseDto> scan(@Valid @RequestBody ScanRequestDto request) {
        ScanResponseDto result = scannerService.scan(request.getUrl(), null);
        return ResponseEntity.ok(result);
    }
}
