package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.PriorityDto;

import java.util.List;

public interface PriorityService {

    // Method create a new priority
    PriorityDto createPriority(PriorityDto dto);

    // Method get by ID
    PriorityDto getPriorityById(Long id);

    // Method update existing
    PriorityDto updatePriority(Long id, PriorityDto dto);

    // Method  delete by ID
    void deletePriority(Long id);

    // Method fetch all
    List<PriorityDto> getAllPriorities();
}