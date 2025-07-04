package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.ProjectAllocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectAllocationHistoryRepository extends JpaRepository<ProjectAllocationHistory, Long> {

}
