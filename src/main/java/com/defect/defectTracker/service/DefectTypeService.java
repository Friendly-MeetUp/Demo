package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.DefectTypeDto;

import java.util.List;

public interface DefectTypeService {

    // Create
    DefectTypeDto createDefectType(DefectTypeDto dto);

    // Update
    DefectTypeDto updateDefectType(Long id, DefectTypeDto dto);

    //Get by ID
    DefectTypeDto getDefectTypeById(Long id);

    // Get all
    List<DefectTypeDto> getAllDefectTypes();

    // Delete by ID
    void deleteDefectType(Long id);
}
