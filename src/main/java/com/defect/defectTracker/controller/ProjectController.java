package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.ProjectDto;
import com.defect.defectTracker.entity.Project;
import com.defect.defectTracker.service.ProjectService;
import com.defect.defectTracker.utils.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

     Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<StandardResponse> createProject(@RequestBody ProjectDto projectDto) {
        try {

            log.info("Creating project with details: {}", projectDto);

            if (projectDto.getProjectName() == null || projectDto.getProjectName().isEmpty()) {
                throw new IllegalArgumentException("Project name is required");
            }
            if (projectDto.getClientName() == null || projectDto.getClientName().isEmpty()) {
                throw new IllegalArgumentException("Client name is required");
            }
            if (projectDto.getCountry() == null || projectDto.getCountry().isEmpty()) {
                throw new IllegalArgumentException("Country is required");
            }
            if (projectDto.getState() == null || projectDto.getState().isEmpty()) {
                throw new IllegalArgumentException("State is required");
            }
            if (projectDto.getEmail() == null || projectDto.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (projectDto.getPhoneNo() == null || projectDto.getPhoneNo().isEmpty()) {
                throw new IllegalArgumentException("Phone number is required");
            }

            Project savedProject = projectService.createProject(projectDto);
            logger.info("Project created successfully with ID: {}", savedProject.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StandardResponse(
                            "success",
                            "Project created successfully",
                            null,
                            2000
                    ));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new StandardResponse(
                            "error",
                            e.getMessage(),
                            null,
                            4000
                    ));
        } catch (DataIntegrityViolationException e) {
            logger.error("Duplicate project userId error: {}", e.getMessage());
            if (e.getMessage().contains("Project with name")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new StandardResponse(
                                "error",
                                "Project with this name already exists",
                                null,
                                4000
                        ));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new StandardResponse(
                            "error",
                            " userId  error: " + e.getMessage(),
                            null,
                            4000
                    ));
        }

    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAllProjects() {
        try {
            List<ProjectDto> projects = projectService.getAllProjects();
            logger.info("Retrieved {} projects successfully", projects.size());
            return ResponseEntity.ok(
                    new StandardResponse(
                            "success",
                            "Projects retrieved successfully",
                            projects,
                            2000
                    ));
        } catch (Exception e) {
            logger.error("Error retrieving projects: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new StandardResponse(
                            "success",
                            "Failed to retrieve projects  "+ e.getMessage(),
                            null,
                            4000
                    ));
        }
    }
}