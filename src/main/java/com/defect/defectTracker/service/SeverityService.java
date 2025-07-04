package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.SeverityDto;
import java.util.List;

public interface SeverityService {
    SeverityDto createSeverity(SeverityDto dto);
    List<SeverityDto> getAllSeverities();
    SeverityDto updateSeverity(Long id, SeverityDto severityDto);
    boolean deleteSeverity(Long id);
}
