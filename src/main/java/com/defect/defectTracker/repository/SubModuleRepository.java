package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.SubModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubModuleRepository extends JpaRepository<SubModule, Long> {                  //submodule repository
    boolean existsBySubModuleName(String subModuleName);

    @Query("SELECT MAX(s.subModuleId) FROM SubModule s")
    List<SubModule> findByModules_ModuleId(Long moduleId);

    List<SubModule> findByModules_Id(Long moduleId);
    boolean existsBySubModuleNameAndModules_ModuleIdAndIdNot(String subModuleName, String moduleId, Long id);


    boolean existsByModules_Id(Long id);
    boolean existsBySubModuleId(String id);
}