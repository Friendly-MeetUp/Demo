package com.defect.defectTracker.dto;


import com.defect.defectTracker.entity.*;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class TestCaseDto {
    private Long id;
    private String testCaseId;
    private String description;
    private String steps;
    private Long subModuleId;
    private Long moduleId;
    private Long projectId;
    private Long severityId;
    private Long defectTypeId;
}
