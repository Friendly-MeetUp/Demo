package com.defect.defectTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor


public class ModulesDto {
    private Long id;

    private String moduleId;


    private String moduleName;


    private Long projectId;
}
