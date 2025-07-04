package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.ReleaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReleaseTypeRepository extends JpaRepository<ReleaseType, Long> {
    boolean existsByReleaseTypeName(String releaseTypeName);
}
