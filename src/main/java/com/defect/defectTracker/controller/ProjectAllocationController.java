package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.ProjectAllocationDto;
import com.defect.defectTracker.service.ProjectAllocationService;
import com.defect.defectTracker.utils.StandardResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projectAllocations")
@RequiredArgsConstructor
public class ProjectAllocationController {

    @Autowired
    ProjectAllocationService projectAllocationService;

    @PostMapping
    public ResponseEntity<StandardResponse> createProjectAllocation(@RequestBody ProjectAllocationDto requestDto) {

        ResponseEntity<StandardResponse> validationResponse = validateProjectAllocationRequest(requestDto);
        if (validationResponse != null) {
            return validationResponse;
        }

        try {
            ProjectAllocationDto created = projectAllocationService.createProjectAllocation(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StandardResponse("success", "Saved Successfully.", created, 2000));
        } catch (RuntimeException ex) {
            return handleProjectAllocationException(ex);
        }
    }

    @DeleteMapping("/{projectAllocationId}")
    public ResponseEntity<StandardResponse> deAllocate(@PathVariable Long projectAllocationId) {
        try {
            StandardResponse response = projectAllocationService.deAllocateEmployee(projectAllocationId);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StandardResponse("failure", "Project allocation not found.", null, 4040));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updateProjectAllocation(
            @PathVariable Long id,
            @RequestBody ProjectAllocationDto requestDto) {

        ResponseEntity<StandardResponse> validationResponse = validateProjectAllocationRequest(requestDto);
        if (validationResponse != null) {
            return validationResponse;
        }

        try {
            projectAllocationService.updateProjectAllocation(id, requestDto);
            return ResponseEntity.ok(new StandardResponse(
                    "success", "Updated Successfully.", null, 2000));
        } catch (RuntimeException ex) {
            return handleProjectAllocationException(ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getProjectAllocationById(@PathVariable Long id) {
        try {
            ProjectAllocationDto dto = projectAllocationService.getProjectAllocationById(id);

            if (dto.getAvailability() != null && dto.getAvailability() < 0) {
                return ResponseEntity.badRequest().body(
                        new StandardResponse(
                                "failure",
                                "Limitation: Maximum allowed allocation is 100%. Current balance: 0%",
                                dto,
                                4000
                        )
                );
            }
            return ResponseEntity.ok(
                    new StandardResponse("success", "Project allocation fetched successfully.", dto, 2000));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StandardResponse("failure", e.getMessage(), null, 4040));
        }
    }

    // Helper methods
    private ResponseEntity<StandardResponse> validateProjectAllocationRequest(ProjectAllocationDto requestDto) {
        if (requestDto == null) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse("failure", "Request body cannot be null", null, 4000));
        }

        if (requestDto.getProjectId() == null || requestDto.getUserId() == null ||
                requestDto.getStartDate() == null || requestDto.getEndDate() == null ||
                requestDto.getAllocationPercentage() == null) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse("failure", "Missing required fields.", null, 4000));
        }

        if (requestDto.getUserId() <= 0) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse("failure", "User ID must be a positive number.", null, 4000));
        }

        if (requestDto.getProjectId() <= 0) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse("failure", "Project ID must be a positive number.", null, 4000));
        }

        if (requestDto.getAllocationPercentage() < 1 || requestDto.getAllocationPercentage() > 100) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse("failure", "Allocation percentage must be between 1 and 100.", null, 4000));
        }

        if (requestDto.getStartDate().after(requestDto.getEndDate())) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse("failure", "Start date must be before end date.", null, 4000));
        }

        return null;
    }

    private ResponseEntity<StandardResponse> handleProjectAllocationException(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Operation failed.";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        int statusCode = 4000;

        if (msg.contains("already exists")) {
            status = HttpStatus.CONFLICT;
            statusCode = 4001;
        } else if (msg.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
            statusCode = 4040;
        }

        return ResponseEntity.status(status)
                .body(new StandardResponse("failure", msg, null, statusCode));
    }
}