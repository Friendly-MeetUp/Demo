package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.UserPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPrivilegeRepository extends JpaRepository<UserPrivilege, Long> {
}
