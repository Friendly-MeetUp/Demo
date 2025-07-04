package com.defect.defectTracker.dto;

import com.defect.defectTracker.entity.TestCase;
import lombok.Data;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefectDto {
    private String defectId;
    private String description;
    private Long projectId; // Mandatory
    private Long severityId;
    private Long priorityId;
    private Long defectStatusId;
    private Long typeId;
    private Long id;
    private int reOpenCount;
    private String attachment;
    private String steps;
    private Long releaseTestCaseId;
    private Long assignbyId;
    private Long assigntoId;
    private Long moduleId;
    private Long subModuleId;
}
//--