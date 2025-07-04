package com.defect.defectTracker.repository;


import com.defect.defectTracker.entity.Modules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ModulesRepository extends JpaRepository<Modules, Long> {
    Optional<Modules> findTopByOrderByModuleIdDesc();
    boolean existsByModuleNameAndProject_Id(String moduleName, Long projectId);
    Optional<Modules> findByModuleNameIgnoreCaseAndProject_Id(String moduleName, Long projectId);
    List<Modules> findByProject_Id(Long projectId);

    List<Modules> findAllByProject_Id(Long projectId);
}