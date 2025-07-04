package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.PriorityDto;
import com.defect.defectTracker.service.PriorityService;
import com.defect.defectTracker.utils.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/priority")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PriorityController {

    private final PriorityService priorityService;

    // Create Priority
    //test
    @PostMapping
    public ResponseEntity<StandardResponse> createPriority(@RequestBody @Valid PriorityDto dto, BindingResult result) {
        log.info("Creating Priority with details: {}", dto);

        if (result.hasErrors()) {
            String errorMessage;
            if (result.getFieldError() != null) {
                errorMessage = result.getFieldError().getDefaultMessage();
            } else {
                errorMessage = "Validation error";
            }
            log.warn("Validation failed: {}", errorMessage);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", errorMessage, null, 4000)
            );
        }
        try {
            PriorityDto created = priorityService.createPriority(dto);
            log.info("Priority created successfully with ID: {}", created.getId());
            return ResponseEntity.ok(new StandardResponse("success", "created successfully", created, 2000));
        } catch (RuntimeException e) {
            log.error("Priority creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", e.getMessage(), null, 4001)
            );
        } catch (Exception e) {
            log.error("Unexpected error while creating Priority", e);
            return ResponseEntity.internalServerError().body(
                    new StandardResponse("error", "creation failed", null, 5000)
            );
        }
    }

    // Get All Priorities
    @GetMapping
    public ResponseEntity<StandardResponse> getAllPriorities() {
        log.info("Fetching all priorities");
        try {
            List<PriorityDto> priorities = priorityService.getAllPriorities();

            if (priorities.isEmpty()) {
                return ResponseEntity.badRequest().body(new StandardResponse("error", "Data not found.", null, 4000));
            }

            return ResponseEntity.ok(new StandardResponse("success", "Retrieved Successfully.", priorities, 2000));
        } catch (Exception e) {
            log.error("Error while fetching all priorities", e);
            return ResponseEntity.internalServerError().body(
                    new StandardResponse("error", "Retrieve Failed.", null, 4000)
            );
        }
    }

    // Get Priority by ID
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getPriorityById(@PathVariable Long id) {
        log.info("Fetching Priority with ID: {}", id);

        // Inline validation
        if (id == null || id <= 0) {
            log.warn("Invalid ID provided: {}", id);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Invalid ID. ID must be a positive number.", null, 4000)
            );
        }

        try {
            PriorityDto priority = priorityService.getPriorityById(id);
            return ResponseEntity.ok(new StandardResponse("success", "Retrieved Successfully.", priority, 2000));
        } catch (RuntimeException e) {
            log.warn("Priority ID not exist: {}", id);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Id not exist.", null, 4000)
            );
        } catch (Exception e) {
            log.error("Error while fetching Priority", e);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Retrieve Failed.", null, 4000)
            );
        }
    }

    // Update Priority
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updatePriority(
            @PathVariable Long id,
            @Valid @RequestBody PriorityDto dto,
            BindingResult result) {

        log.info("Updating Priority with ID: {}", id);

        // Check for validation errors in the DTO
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError() != null ? result.getFieldError().getDefaultMessage() : "Validation error";
            log.warn("Validation failed: {}", errorMessage);
            return ResponseEntity.badRequest().body(new StandardResponse("error", errorMessage, null, 4000));
        }

        try {
            // Call service layer to perform the update
            PriorityDto updatedPriority = priorityService.updatePriority(id, dto);
            return ResponseEntity.ok(new StandardResponse("success", "Updated successfully", updatedPriority, 2000));
        } catch (RuntimeException e) {
            // Handle specific errors and return meaningful responses
            String errorMessage = e.getMessage();  // Get the message from the exception
            log.error("Error during update: {}", errorMessage);

            if (errorMessage.contains("Priority not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StandardResponse("error", errorMessage, null, 4001));
            } else if (errorMessage.contains("Priority name already exists")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StandardResponse("error", errorMessage, null, 4002));
            } else if (errorMessage.contains("Priority color already exists")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StandardResponse("error", errorMessage, null, 4003));
            } else {
                // If it's some other error, return a general failure response
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StandardResponse("error", "Update failed due to an unknown error", null, 5000));
            }
        }
    }

    //  Delete Priority
        @DeleteMapping("/{id}")
        public ResponseEntity<StandardResponse> deletePriority (@PathVariable Long id){
            log.info("Deleting Priority with ID: {}", id);
            try {
                priorityService.deletePriority(id);
                return ResponseEntity.ok(new StandardResponse("success", "Deleted successfully.", null, 2000));
            } catch (RuntimeException e) {
                log.warn("Priority not found for deletion: {}", id);
                return ResponseEntity.badRequest().body(new StandardResponse("error", "Id not exist.", null, 4000));
            } catch (Exception e) {
                log.error("Error while deleting Priority", e);
                return ResponseEntity.badRequest().body(new StandardResponse("error", "Delete Failed.", null, 4000));
            }
        }
    }
