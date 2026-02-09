package com.hopl.repository;

import com.hopl.model.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    List<ScanResult> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<ScanResult> findFirstByUrlAndCreatedAtAfterOrderByCreatedAtDesc(String url, LocalDateTime after);
}
