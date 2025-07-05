package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ProjectAllocationDto;
import com.defect.defectTracker.entity.*;
import com.defect.defectTracker.entity.ProjectAllocation;
import com.defect.defectTracker.repository.*;
import com.defect.defectTracker.utils.StandardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.defect.defectTracker.entity.Role;
import java.util.List;

import com.defect.defectTracker.repository.ProjectRepository;

import java.util.Objects;

@Service
@Transactional
public class ProjectAllocationServiceImpl implements ProjectAllocationService {

    Logger logger = LoggerFactory.getLogger(ProjectAllocationServiceImpl.class);

    @Autowired
    private ProjectAllocationRepository projectAllocationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BenchRepository benchRepository;

    private String generateNextBenchId() {
        Long maxId = projectAllocationRepository.findMaxId();
        long nextNumericId = (maxId == null) ? 1 : maxId + 1;
        return String.format("BE%04d", nextNumericId);
    }

    @Override
    public ProjectAllocationDto createProjectAllocation(ProjectAllocationDto requestDto) {
        validateRequestDto(requestDto);
        User user = getUserById(requestDto.getUserId());
        Project project = getProjectById(requestDto.getProjectId());
        Role role = getRole(requestDto);

        validateExistingAllocation(user, project);
        validateAllocationPercentage(user.getId(), requestDto.getAllocationPercentage(), 0);

        ProjectAllocation allocationEntity = buildAllocationEntity(requestDto, user, project, role);
        ProjectAllocation savedAllocation = projectAllocationRepository.save(allocationEntity);

        updateBenchAllocation(user);

        logger.info("Project allocation created successfully for userId: {}, projectId: {}", user.getUserId(), project.getId());
        return buildResponseDto(savedAllocation);
    }

    @Override
    public ProjectAllocationDto getProjectAllocation(Long allocationId, Long projectId) {
        ProjectAllocation allocation = projectAllocationRepository.findByAllocationIdAndProjectId(allocationId, projectId)
                .orElseThrow(() -> new RuntimeException("No matching records found."));
        return buildResponseDto(allocation);
    }

    @Override
    public ProjectAllocationDto getProjectAllocationById(Long id) {
        ProjectAllocation allocation = projectAllocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project allocation not found with ID: " + id));
        return buildResponseDto(allocation);
    }

    @Override
    public StandardResponse deAllocateEmployee(Long allocationId) {
        ProjectAllocation allocation = projectAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Project allocation not found with ID: " + allocationId));

        User user = allocation.getUser();
        projectAllocationRepository.delete(allocation);
        updateBenchAllocation(user);

        int available = 100 - projectAllocationRepository.sumAllocationPercentageByUserId(user.getId());
        return new StandardResponse("success", "De-Allocated Successfully. Availability: " + available + "%", null, 2000);
    }

    @Override
    public ProjectAllocationDto updateProjectAllocation(Long id, ProjectAllocationDto requestDto) {
        logger.info("Updating project allocation with ID: {}", id);

        if (requestDto == null) {
            logger.error("Request body is null");
            throw new IllegalArgumentException("Request body cannot be null");
        }

        ProjectAllocation existingAllocation = projectAllocationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Project allocation not found with ID: {}", id);
                    return new RuntimeException("Project allocation not found with ID: " + id);
                });

        boolean isUpdated = updateAllocationFields(requestDto, existingAllocation);

        if (!isUpdated) {
            logger.info("No changes detected for allocation ID: {}", id);
            ProjectAllocationDto responseDto = buildResponseDto(existingAllocation);
            throw new AlreadyExistsException("No changes detected. The provided values already exist.", responseDto);
        }

        ProjectAllocation updatedAllocation = projectAllocationRepository.save(existingAllocation);
        updateBenchAllocation(updatedAllocation.getUser());

        logger.info("Successfully updated allocation for user {}", updatedAllocation.getUser().getId());
        return buildResponseDto(updatedAllocation);
    }

    @Override
    public int getTotalAllocationPercentageByUserId(Long userId) {
        int total = projectAllocationRepository.sumAllocationPercentageByUserId(userId);
        return Math.max(0, total);
    }

    // Helper methods
    private void validateRequestDto(ProjectAllocationDto requestDto) {
        if (requestDto.getUserId() == null || requestDto.getUserId() <= 0) {
            throw new RuntimeException("User ID must be a positive number.");
        }
        if (requestDto.getProjectId() == null || requestDto.getProjectId() <= 0) {
            throw new RuntimeException("Project ID must be a positive number.");
        }
        if (requestDto.getRoleId() == null && requestDto.getRoleName() == null) {
            logger.error("Role information is required");
            throw new RuntimeException("Role information is required");
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    logger.error("Project not found with ID: {}", projectId);
                    return new RuntimeException("Project not found with ID: " + projectId);
                });
    }

    private Role getRole(ProjectAllocationDto requestDto) {
        if (requestDto.getRoleId() != null) {
            return roleRepository.findById(requestDto.getRoleId())
                    .orElseThrow(() -> {
                        logger.error("Role not found with ID: {}", requestDto.getRoleId());
                        return new RuntimeException("Role not found with ID: " + requestDto.getRoleId());
                    });
        } else {
            return roleRepository.findByRoleName(requestDto.getRoleName())
                    .orElseThrow(() -> {
                        logger.error("Role not found with name: {}", requestDto.getRoleName());
                        return new RuntimeException("Role not found with name: " + requestDto.getRoleName());
                    });
        }
    }

    private void validateExistingAllocation(User user, Project project) {
        if (projectAllocationRepository.existsByUserAndProject(user, project)) {
            logger.warn("Project allocation already exists for userId: {}, projectId: {}", user.getId(), project.getId());
            throw new RuntimeException("Project allocation already exists for this user and project");
        }
    }

    private void validateAllocationPercentage(Long userId, int requestedPercentage, int currentAllocation) {
        int currentTotal = projectAllocationRepository.sumAllocationPercentageByUserId(userId);
        int remaining = 100 - (currentTotal - currentAllocation);

        if (requestedPercentage > remaining) {
            logger.error("User allocation would exceed 100%: current={}, requested={}, remaining={}",
                    currentTotal, requestedPercentage, remaining);
            throw new RuntimeException("Maximum allowed allocation is 100%. Current availability: " + remaining + "%");
        }
    }

    private ProjectAllocation buildAllocationEntity(ProjectAllocationDto requestDto, User user, Project project, Role role) {
        ProjectAllocation allocationEntity = new ProjectAllocation();
        allocationEntity.setAllocationPercentage(requestDto.getAllocationPercentage());
        allocationEntity.setStartDate(requestDto.getStartDate());
        allocationEntity.setEndDate(requestDto.getEndDate());
        allocationEntity.setUser(user);
        allocationEntity.setProject(project);
        allocationEntity.setRole(role);
        return allocationEntity;
    }

    private void updateBenchAllocation(User user) {
        int totalAllocated = projectAllocationRepository.sumAllocationPercentageByUserId(user.getId());
        List<Bench> benches = benchRepository.findByUser(user);
        Bench bench = benches.isEmpty() ? null : benches.get(0);

        if (totalAllocated >= 100) {
            if (bench != null) {
                benchRepository.delete(bench);
            }
        } else {
            if (bench == null) {
                bench = new Bench();
                bench.setUser(user);
                bench.setBenchId(java.util.UUID.randomUUID().toString());
                bench.setBenchId(generateNextBenchId());
            }
            bench.setAllocated(totalAllocated);
            bench.setAvailability(100 - totalAllocated);
            benchRepository.save(bench);
        }
    }

    private boolean updateAllocationFields(ProjectAllocationDto requestDto, ProjectAllocation existingAllocation) {
        boolean isUpdated = false;
        int oldPercentage = existingAllocation.getAllocationPercentage();

        if (requestDto.getAllocationPercentage() != null &&
                !requestDto.getAllocationPercentage().equals(oldPercentage)) {
            validateAllocationPercentage(existingAllocation.getUser().getId(),
                    requestDto.getAllocationPercentage(), oldPercentage);
            existingAllocation.setAllocationPercentage(requestDto.getAllocationPercentage());
            isUpdated = true;
        }

        if (requestDto.getStartDate() != null &&
                !requestDto.getStartDate().equals(existingAllocation.getStartDate())) {
            existingAllocation.setStartDate(requestDto.getStartDate());
            isUpdated = true;
        }

        if (requestDto.getEndDate() != null &&
                !requestDto.getEndDate().equals(existingAllocation.getEndDate())) {
            if (requestDto.getStartDate() != null &&
                    requestDto.getEndDate().before(requestDto.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date");
            }
            existingAllocation.setEndDate(requestDto.getEndDate());
            isUpdated = true;
        }

        if (requestDto.getRoleId() != null || requestDto.getRoleName() != null) {
            Role role = getRole(requestDto);
            if (existingAllocation.getRole() == null ||
                    !Objects.equals(existingAllocation.getRole().getId(), role.getId())) {
                existingAllocation.setRole(role);
                isUpdated = true;
            }
        }

        return isUpdated;
    }

    private ProjectAllocationDto buildResponseDto(ProjectAllocation allocation) {
        ProjectAllocationDto dto = new ProjectAllocationDto();
        dto.setId(allocation.getId());

        if (allocation.getProject() != null) {
            dto.setProjectId(allocation.getProject().getId());
            dto.setProjectName(allocation.getProject().getProjectName());
        }

        if (allocation.getUser() != null) {
            dto.setUserId(allocation.getUser().getId());
            String fullName = (allocation.getUser().getFirstName() != null ? allocation.getUser().getFirstName() : "") +
                    " " +
                    (allocation.getUser().getLastName() != null ? allocation.getUser().getLastName() : "");
            dto.setUserFullName(fullName.trim());
        }

        dto.setStartDate(allocation.getStartDate());
        dto.setEndDate(allocation.getEndDate());
        dto.setAllocationPercentage(allocation.getAllocationPercentage());

        if (allocation.getRole() != null) {
            dto.setRoleId(allocation.getRole().getId());
            dto.setRoleName(allocation.getRole().getRoleName());
        }

        int totalAllocated = projectAllocationRepository.sumAllocationPercentageByUserId(allocation.getUser().getId());
        dto.setAvailability(Math.max(0, 100 - totalAllocated));

        return dto;
    }

    public static class AlreadyExistsException extends RuntimeException {
        private final ProjectAllocationDto data;

        public AlreadyExistsException(String message, ProjectAllocationDto data) {
            super(message);
            this.data = data;
        }

        public ProjectAllocationDto getData() {
            return data;
        }
    }
}