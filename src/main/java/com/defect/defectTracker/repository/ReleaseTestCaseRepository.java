package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.ReleaseTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReleaseTestCaseRepository extends JpaRepository<ReleaseTestCase, Long> {
    Optional<ReleaseTestCase> findById(Long id);
    boolean existsByReleasesIdAndTestCaseId(Long id, Long id1);

    ReleaseTestCase findByReleasesIdAndTestCaseId(Long id, Long id1);
    List<ReleaseTestCase> findByTestCase_Description(String description);

}
