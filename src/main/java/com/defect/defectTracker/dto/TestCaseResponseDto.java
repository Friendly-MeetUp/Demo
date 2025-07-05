package com.defect.defectTracker.dto;

import lombok.Data;

@Data
public class TestCaseResponseDto {
    private Long id;
    private String description;
    private String steps;
    private String testCaseId;
    private String moduleId;
    private String projectId;
    private String severityId;
    private String subModuleId;
    private Long typeId;
    private String releaseId;
}