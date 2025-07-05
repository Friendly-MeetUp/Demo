package com.defect.defectTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubModuleDto {                                            //submodule dto
    private Long subModuleId;
    private String subModuleName;
    private Long moduleId;
    private String moduleName;
}