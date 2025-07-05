package com.defect.defectTracker.repository;


import com.defect.defectTracker.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {
    boolean existsByDesignation(String designation);
}
