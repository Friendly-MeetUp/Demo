package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ModulesDto;
import com.defect.defectTracker.entity.Modules;

import java.util.List;

public interface ModuleService {
    Modules createModule(ModulesDto modulesDto);
    boolean checkIfModuleExists(String moduleName, Long projectId);
    Modules updateModule(ModulesDto modulesDto);
    boolean deleteModuleById(Long id);
    boolean isDuplicateModuleNameForUpdate(Long currentModuleId, String newModuleName, Long projectId);
    public boolean checkIfModuleExistsById(Long id);
    List<ModulesDto> getAllModulesByProjectId(Long projectId);
    ModulesDto getModuleById(Long moduleId); // get module id
}
