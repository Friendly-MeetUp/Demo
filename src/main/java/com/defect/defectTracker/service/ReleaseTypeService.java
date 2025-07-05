package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ReleaseTypeDto;

import java.util.List;

public interface ReleaseTypeService {
    ReleaseTypeDto createReleaseType(ReleaseTypeDto dto);
    List<ReleaseTypeDto> getAllReleaseTypes();
    ReleaseTypeDto getReleaseTypeById(Long id);
    ReleaseTypeDto updateReleaseType(Long id, ReleaseTypeDto dto);
    boolean deleteReleaseType(Long id);
}