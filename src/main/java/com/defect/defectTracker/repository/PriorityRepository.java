package com.defect.defectTracker.repository;

import com.defect.defectTracker.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {
    boolean existsByPriority(String priority);
    boolean existsByColor(String color);
    Optional<Priority> findByPriorityIgnoreCase(String priority);
    Optional<Priority> findByColorIgnoreCase(String color);
    Optional<Priority> findByPriority(String priority);

}
