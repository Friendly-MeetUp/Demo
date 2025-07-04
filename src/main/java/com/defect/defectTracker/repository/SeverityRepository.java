package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeverityRepository extends JpaRepository<Severity, Long> {
    boolean existsBySeverityName(String severityName);
    boolean existsByIdNotAndSeverityName(Long id, String severityName);
}