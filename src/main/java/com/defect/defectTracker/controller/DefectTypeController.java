package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.DefectTypeDto;
import com.defect.defectTracker.service.DefectTypeService;
import com.defect.defectTracker.utils.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/defectType")
public class DefectTypeController {

    private final DefectTypeService defectTypeService;

    // Create
    @PostMapping
    public ResponseEntity<StandardResponse> create(@Valid @RequestBody DefectTypeDto dto) {
        log.info("Received request to create DefectType: {}", dto.getDefectTypeName());
        try {
            DefectTypeDto created = defectTypeService.createDefectType(dto);
            return ResponseEntity.ok(new StandardResponse("success", "Saved successfully", created, 2000));
        } catch (RuntimeException e) {
            log.warn("Create failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new StandardResponse("error", e.getMessage(), null, 4000));
        } catch (Exception e) {
            log.error("Unexpected error during create", e);
            return ResponseEntity.badRequest().body(new StandardResponse("error", "Something went wrong while saving. Please try again later.", null, 4000));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse> handleValidationError(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Invalid input. Please check the submitted data.");

        log.warn("Validation error in request body: {}", errorMessage);

        return ResponseEntity.badRequest().body(
                new StandardResponse("error", errorMessage, null, 4000)
        );
    }
    // Update
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody DefectTypeDto dto) {
        log.info("Received request to update DefectType ID: {}", id);
        try {
            DefectTypeDto updated = defectTypeService.updateDefectType(id, dto);
            return ResponseEntity.ok(new StandardResponse("success", "Updated successfully", updated, 2000));
        } catch (RuntimeException e) {
            log.warn("Update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new StandardResponse("error", e.getMessage(), null, 4000));
        } catch (Exception e) {
            log.error("Unexpected error during update", e);
            return ResponseEntity.badRequest().body(new StandardResponse("error", "Something went wrong while updating. Please try again later.", null, 4000));
        }
    }

    // Get  by ID
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getById(@PathVariable Long id) {
        log.info("Fetching DefectType by ID: {}", id);
        try {
            DefectTypeDto found = defectTypeService.getDefectTypeById(id);
            return ResponseEntity.ok(new StandardResponse("success", "Retrieved successfully", found, 2000));
        } catch (RuntimeException e) {
            log.warn("Get by ID failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new StandardResponse("error", e.getMessage(), null, 4000));
        } catch (Exception e) {
            log.error("Unexpected error during fetch by ID", e);
            return ResponseEntity.badRequest().body(new StandardResponse("error", "Something went wrong while retrieving. Please try again later.", null, 4000));
        }
    }

    // Get All
    @GetMapping
    public ResponseEntity<StandardResponse> getAll() {
        log.info("Fetching all DefectTypes");
        try {
            List<DefectTypeDto> list = defectTypeService.getAllDefectTypes();
            return ResponseEntity.ok(new StandardResponse("success", "Retrieved successfully", list, 2000));
        } catch (RuntimeException e) {
            log.warn("Get all failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new StandardResponse("error", e.getMessage(), null, 4000));
        } catch (Exception e) {
            log.error("Unexpected error during fetch all", e);
            return ResponseEntity.badRequest().body(new StandardResponse("error", "Something went wrong while retrieving. Please try again later.", null, 4000));
        }
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> delete(@PathVariable Long id) {
        log.info("Deleting DefectType with ID: {}", id);
        try {
            defectTypeService.deleteDefectType(id);
            return ResponseEntity.ok(new StandardResponse("success", "Deleted successfully", null, 2000));
        } catch (RuntimeException e) {
            log.warn("Delete failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new StandardResponse("error", e.getMessage(), null, 4000));
        } catch (Exception e) {
            log.error("Unexpected error during delete", e);
            return ResponseEntity.badRequest().body(new StandardResponse("error", "Something went wrong while deleting. Please try again later.", null, 4000));
        }
    }
}
