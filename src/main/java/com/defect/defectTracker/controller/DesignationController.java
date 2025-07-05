package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.DesignationDto;
import com.defect.defectTracker.service.DesignationService;
import com.defect.defectTracker.utils.StandardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/designation")
public class DesignationController {

     Logger logger = LoggerFactory.getLogger(DesignationController.class);

    @Autowired
    private DesignationService designationService;



    @PostMapping
    public ResponseEntity<StandardResponse> createDesignation(@RequestBody DesignationDto dto) {
        logger.info("Creating designation with details: {}", dto);
        try {
            DesignationDto created = designationService.createDesignation(dto);
            logger.info("Designation created successfully with ID: {}", created);

            StandardResponse response = new StandardResponse(
                    "SUCCESS",
                    "Designation created successfully",
                    created,
                    200
            );
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException ex) {
            logger.error("Error creating designation: {}", ex.getMessage(), ex);

            if (ex.getMessage() != null && ex.getMessage().contains("Designation unique")) {
                StandardResponse errorResponse = new StandardResponse(
                        "ERROR",
                        ex.getMessage(),
                        null,
                        400
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updateDesignation(
            @PathVariable Long id,
            @RequestBody DesignationDto dto) {

        logger.info("Updating designation with ID: {} and details: {}", id, dto);

        try {
            DesignationDto updated = designationService.updateDesignation(id, dto);
            StandardResponse response = new StandardResponse(
                    "SUCCESS",
                    "Designation updated successfully",
                    updated,
                    200
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Error updating designation: {}", ex.getMessage(), ex);

            if (ex.getMessage() != null && ex.getMessage().contains("unique")) {
                StandardResponse errorResponse = new StandardResponse(
                        "ERROR",
                        ex.getMessage(),
                        null,
                        400
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getDesignationById(@PathVariable Long id) {
        logger.info("Fetching designation with ID: {}", id);
        DesignationDto dto = designationService.getDesignationById(id);

        StandardResponse response = new StandardResponse(
                "SUCCESS",
                "Designation fetched successfully",
                dto,
                200
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deleteDesignation(@PathVariable Long id) {
        logger.info("Deleting designation with ID: {}", id);
        boolean deleted = designationService.deleteDesignation(id);

        if (deleted) {
            StandardResponse response = new StandardResponse(
                    "SUCCESS",
                    "Designation deleted successfully",
                    null,
                    200
            );
            return ResponseEntity.ok(response);
        } else {
            StandardResponse response = new StandardResponse(
                    "ERROR",
                    "Delete failed: designation not found",
                    null,
                    400
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAllDesignations() {
        logger.info("Fetching all designations");
        List<DesignationDto> designations = designationService.getAllDesignations();

        StandardResponse response = new StandardResponse(
                "SUCCESS",
                "All designations fetched successfully",
                designations,
                200
        );
        return ResponseEntity.ok(response);
    }


}
