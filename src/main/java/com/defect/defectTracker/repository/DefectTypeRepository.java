package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.Defect;
import jakarta.transaction.Transactional;
import com.defect.defectTracker.entity.DefectStatus;
import com.defect.defectTracker.entity.DefectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Transactional
@Repository
public interface DefectTypeRepository extends JpaRepository<DefectType, Long> {
    boolean existsByDefectTypeNameIgnoreCase(String defectTypeName);
}