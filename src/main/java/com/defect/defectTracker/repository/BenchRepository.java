package com.defect.defectTracker.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.defect.defectTracker.entity.Bench;
import com.defect.defectTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BenchRepository extends JpaRepository<Bench, Long>, JpaSpecificationExecutor<Bench> {
    List<Bench> findByUser(User user);
}