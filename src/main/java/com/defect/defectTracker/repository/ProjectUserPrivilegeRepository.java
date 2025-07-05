package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.ProjectUserPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectUserPrivilegeRepository extends JpaRepository<ProjectUserPrivilege, Long> {
}
