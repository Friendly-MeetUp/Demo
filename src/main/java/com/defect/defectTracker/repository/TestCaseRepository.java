package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProjectIdAndModulesId(Long projectId, Long moduleId);

    // Get test cases by project and submodule
    @Query("SELECT t FROM TestCase t WHERE t.project.id = :projectId AND t.subModule.id = :submoduleId")
    List<TestCase> findByProjectIdAndSubmoduleId(@Param("projectId") Long projectId,
                                                 @Param("submoduleId") Long submoduleId);

    // Search test cases by filters
    @Query("SELECT t FROM TestCase t WHERE " +
            "(:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:typeId IS NULL OR t.defectType.id = :typeId) AND " +
            "(:severityId IS NULL OR t.severity.id = :severityId) AND " +
            "(:submoduleId IS NULL OR t.subModule.id = :submoduleId)")
    List<TestCase> searchTestCases(@Param("description") String description,
                                   @Param("typeId") Long typeId,
                                   @Param("severityId") Long severityId,
                                   @Param("submoduleId") Long submoduleId);

    // Find test cases by project, module, submodule and release
    @Query("SELECT tc FROM TestCase tc " +
            "WHERE tc.project.id = :projectId " +
            "AND tc.modules.id = :moduleId " +
            "AND tc.subModule.id = :submoduleId " +
            "AND EXISTS (SELECT rtc FROM ReleaseTestCase rtc WHERE rtc.releases.id = :releaseId AND rtc.testCase.id = tc.id)")
    List<TestCase> findByProjectAndModuleAndSubmoduleAndRelease(@Param("projectId") Long projectId,
                                                                @Param("moduleId") Long moduleId,
                                                                @Param("submoduleId") Long submoduleId,
                                                                @Param("releaseId") Long releaseId);


    @Query(value = "SELECT MAX(id) FROM test_case", nativeQuery = true)
    Long findTopByOrderByIdDesc();

    List<TestCase> findByIdIn(List<Long> testCaseIds);
}