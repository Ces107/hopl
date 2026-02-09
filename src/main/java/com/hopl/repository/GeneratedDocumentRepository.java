package com.hopl.repository;

import com.hopl.model.GeneratedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, Long> {
    List<GeneratedDocument> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<GeneratedDocument> findByScanId(Long scanId);
}
