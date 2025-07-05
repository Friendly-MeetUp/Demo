package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.Project;
import com.defect.defectTracker.entity.ProjectAllocation;
import com.defect.defectTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectAllocationRepository extends JpaRepository<ProjectAllocation, Long> {

    @Query("SELECT pa FROM ProjectAllocation pa " +
            "JOIN FETCH pa.user u " +
            "JOIN FETCH pa.project p " +
            "JOIN FETCH pa.role r " +
            "WHERE u.userId = :userId")
    List<ProjectAllocation> findByUserId(String userId);

    @Query("SELECT pa FROM ProjectAllocation pa " +
            "JOIN pa.user u " +
            "JOIN Bench b ON b.user = u " +
            "WHERE b.user.userId = :userId")
    List<ProjectAllocation> findByBenchUserId(String userId);

    boolean existsByUserUserIdAndProjectProjectName(String userId, String projectName);

    java.util.Optional<ProjectAllocation> findByIdAndProjectId(Long id, Long projectId);

    @Query("SELECT pa FROM ProjectAllocation pa " +
            "JOIN FETCH pa.user u " +
            "JOIN FETCH pa.project p " +
            "JOIN FETCH pa.role r " +
            "WHERE pa.id = :allocationId AND p.id = :projectId")
    java.util.Optional<ProjectAllocation> findByAllocationIdAndProjectId(@Param("allocationId") Long allocationId, @Param("projectId") Long projectId);

    List<ProjectAllocation> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(pa.allocationPercentage), 0) FROM ProjectAllocation pa WHERE pa.user.id = :userId")
    int sumAllocationPercentageByUserId(@Param("userId") Long userId);

    boolean existsByUserAndProject(User user, Project project);
}