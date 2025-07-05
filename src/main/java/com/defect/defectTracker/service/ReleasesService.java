package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ReleasesDto;
import com.defect.defectTracker.entity.Releases;

import java.util.List;

public interface ReleasesService {
    Releases createRelease(ReleasesDto releaseDto);

    List<ReleasesDto> getReleasesByProjectId(Long projectId);

}