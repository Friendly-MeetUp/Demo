package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.Defect;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.Optional;

@Transactional
@Repository

public interface DefectRepository extends JpaRepository<Defect, Long> {
    @Query(value = "SELECT d.defectId FROM Defect d  ORDER BY d.id DESC LIMIT 1")
    String findTopByOrderByIdDesc();
//----
    Optional<Defect> findById(Long id);

    List<Defect> findByProjectId(Long projectId);

    Defect findByReleaseTestCaseId(Long releaseTestCaseId);

    boolean existsByReleaseTestCaseId(Long releaseTestCaseId);
    // Updated JPQL query navigating through relationships
    @Query("SELECT d FROM Defect d WHERE " +
            "(:projectId IS NULL OR d.project.id = :projectId) AND " +
            "(:defectStatusId IS NULL OR d.defectStatus.id = :defectStatusId) AND " +
            "(:severityId IS NULL OR d.severity.id = :severityId) AND " +
            "(:priorityId IS NULL OR d.priority.id = :priorityId) AND " +
            "(:typeId IS NULL OR d.defectType.id = :typeId) AND " +
            "(:releaseTestCaseId IS NULL OR d.releaseTestCase.id = :releaseTestCaseId) ")
//            +
//            "AND " +
//            "(:AssignbyId IS NULL OR d.assignBy.id = :AssignbyId) AND " +
//            "(:AssigntoId IS NULL OR d.assignTo.id = :AssigntoId)")


    List<Defect> filterDefects(
            @Param("projectId") Long projectId,
            @Param("defectStatusId") Long defectStatusId,
            @Param("severityId") Long severityId,
            @Param("priorityId") Long priorityId,
            @Param("typeId") Long typeId,
            @Param("releaseTestCaseId") Long releaseTestCaseId,
            @Param("AssignbyId") Long AssignbyId,
            @Param("AssigntoId") Long AssigntoId);


    @Query("SELECT d FROM Defect d WHERE " +
            "(:projectId IS NULL OR d.project.id = :projectId) AND " +
            "(:defectStatusId IS NULL OR d.defectStatus.id = :defectStatusId) AND " +
            "(:severityId IS NULL OR d.severity.id = :severityId) AND " +
            "(:priorityId IS NULL OR d.priority.id = :priorityId) AND " +
            "(:typeId IS NULL OR d.defectType.id = :typeId)")
    List<Defect> filterDefects(
            @Param("projectId") Long projectId,
            @Param("defectStatusId") Long defectStatusId,
            @Param("severityId") Long severityId,
            @Param("priorityId") Long priorityId,
            @Param("typeId") Long typeId);

}