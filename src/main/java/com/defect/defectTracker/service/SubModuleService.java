package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.SubModuleDto;
import com.defect.defectTracker.utils.StandardResponse;

import java.util.List;

public interface SubModuleService {
    StandardResponse updateSubModule(Long id, SubModuleDto dto) throws Exception;
    StandardResponse createSubModule(SubModuleDto dto);
    List<SubModuleDto> getSubModulesByModuleId(Long moduleId);
    void deleteSubModuleBySubModuleId(Long Id);

}