package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {


    @Query(value = "SELECT MAX(id) FROM project", nativeQuery = true)
    Long findMaxId();

    Optional<Project> findByProjectName(String projectName);
    Optional<Project> findByProjectId(String projectId);
    Optional<Project> findById(Long id);

    Optional<Object> findByUserId(Long userId);
}