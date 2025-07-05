package com.defect.defectTracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestCaseAllocationDto {
    // Request fields
    private List<Long> testCaseIds;
    private Long releaseId;

    // Response fields
    private int allocatedCount;
    private int failedCount;
}