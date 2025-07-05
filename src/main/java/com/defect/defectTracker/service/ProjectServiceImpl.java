package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ProjectDto;
import com.defect.defectTracker.entity.Project;
import com.defect.defectTracker.entity.User;
import com.defect.defectTracker.repository.ProjectRepository;
import com.defect.defectTracker.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;




@Service
public class ProjectServiceImpl implements ProjectService {

    Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;


    private String generateNextProjectId() {
        Long maxId = projectRepository.findMaxId();
        long nextNumericId = (maxId == null) ? 1 : maxId + 1;
        return String.format("PR%04d", nextNumericId);
    }

    @Override
    public Project createProject(ProjectDto projectDto) {
        logger.info("Creating project  started : {}", projectDto);
        if (projectDto.getProjectName() == null || projectDto.getProjectName().isEmpty()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (projectRepository.findByProjectName(projectDto.getProjectName()).isPresent()) {
            throw new DataIntegrityViolationException("Project with name '" + projectDto.getProjectName() + "' already exists.");
        }


//        User user = userRepository.findById(projectDto.getUserId())
//                .orElseThrow(() -> new DataIntegrityViolationException("User not found with id: " + projectDto.getUserId()));

        String newProjectId = generateNextProjectId();

        if (projectRepository.findByProjectId(newProjectId).isPresent()) {

            throw new DataIntegrityViolationException("Generated Project ID '" + newProjectId + "' already exists. Please try again.");
        }

        Project project = new Project();
        project.setProjectName(projectDto.getProjectName());
        project.setDescription(projectDto.getDescription());
        project.setStartDate(projectDto.getStartDate());
        project.setEndDate(projectDto.getEndDate());
        project.setClientName(projectDto.getClientName());
        project.setCountry(projectDto.getCountry());
        project.setState(projectDto.getState());
        project.setEmail(projectDto.getEmail());
        project.setPhoneNo(projectDto.getPhoneNo());
        project.setProjectId(newProjectId);
        User user=new User();
        user.setId(projectDto.getUserId());
        project.setUser(user);

        try {
            logger.info("user: {}", user.getId());
            logger.info("Saving project with ID: {}", newProjectId);
            logger.info("project : {}", project.getProjectId());
             return projectRepository.save(project);

        } catch (DataIntegrityViolationException e) {

            throw new DataIntegrityViolationException("Could not create project due to data integrity violation: " + e.getMessage());
        }
    }

    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        if (projects.isEmpty()) {
            throw new DataIntegrityViolationException("No projects found.");
        }

        return projects.stream()
                .map(project -> {
                    ProjectDto projectDto = new ProjectDto();
                    projectDto.setId(project.getId());
                    projectDto.setProjectId(project.getProjectId());
                    projectDto.setProjectName(project.getProjectName());
                    projectDto.setDescription(project.getDescription());
                    projectDto.setStartDate(project.getStartDate());
                    projectDto.setEndDate(project.getEndDate());
                    projectDto.setClientName(project.getClientName());
                    projectDto.setState(project.getState());
                    projectDto.setPhoneNo(project.getPhoneNo());
                    projectDto.setEmail(project.getEmail());
                    projectDto.setCountry(project.getCountry());
                    projectDto.setUserId(project.getUser().getId());


                    User user = userRepository.findById(project.getUser().getId()).orElse(null);
                    if (user != null) {
                        projectDto.setUserFirstName(user.getFirstName());
                        projectDto.setUserLastName(user.getLastName());
                    }

                    return projectDto;
                })
                .collect(Collectors.toList());
    }
}

