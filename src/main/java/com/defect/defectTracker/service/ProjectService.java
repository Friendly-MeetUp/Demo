package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ProjectDto;
import com.defect.defectTracker.entity.Project;

import java.util.List;


public interface ProjectService {
    Project createProject(ProjectDto projectDto);
    List<ProjectDto> getAllProjects();

}