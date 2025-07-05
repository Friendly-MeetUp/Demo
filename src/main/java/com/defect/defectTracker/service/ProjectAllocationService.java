package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ProjectAllocationDto;
import com.defect.defectTracker.utils.StandardResponse;

public interface ProjectAllocationService {
    ProjectAllocationDto createProjectAllocation(ProjectAllocationDto projectAllocationDto);
    ProjectAllocationDto getProjectAllocation(Long allocationId, Long projectId);
    ProjectAllocationDto getProjectAllocationById(Long id);
    StandardResponse deAllocateEmployee(Long allocationId);
    ProjectAllocationDto updateProjectAllocation(Long id, ProjectAllocationDto projectAllocationDto);
    int getTotalAllocationPercentageByUserId(Long userId);
}