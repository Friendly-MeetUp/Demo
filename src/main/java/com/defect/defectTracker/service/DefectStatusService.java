package com.defect.defectTracker.service;


import com.defect.defectTracker.dto.DefectStatusDto;

import java.util.List;

public interface DefectStatusService {
    DefectStatusDto createDefectStatus(DefectStatusDto dto);
    List<DefectStatusDto> getAllDefectStatuses();
    DefectStatusDto getDefectStatusById(Long id);
    DefectStatusDto updateDefectStatus(Long id, DefectStatusDto dto);
    DefectStatusDto deleteDefectStatus(Long id);
}