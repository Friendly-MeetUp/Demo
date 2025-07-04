package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.DesignationDto;
import java.util.List;

public interface DesignationService {
    DesignationDto createDesignation(DesignationDto dto);
    DesignationDto updateDesignation(Long id, DesignationDto dto);
    DesignationDto getDesignationById(Long id);
    boolean deleteDesignation(Long id);
    List<DesignationDto> getAllDesignations();
}
