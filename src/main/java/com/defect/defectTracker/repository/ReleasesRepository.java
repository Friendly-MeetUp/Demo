package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.Releases;
import com.defect.defectTracker.entity.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReleasesRepository extends JpaRepository<Releases, Long> {
    Optional<Object> findByReleaseName(String releaseName);
    @Query(value = "SELECT MAX(id) FROM releases", nativeQuery = true)
    Long findMaxId();

    Optional<Releases> findByReleaseNameAndProjectId(String releaseName, Long projectId);

    List<Releases> findByProjectId(Long projectId);

    List<Releases> findByReleaseNameContainingIgnoreCase(String releaseName);

    List<Releases> findByProjectIdAndReleaseNameContainingIgnoreCase(Long projectId, String trim);
}
