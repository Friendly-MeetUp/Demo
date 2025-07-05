package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.SeverityDto;
import com.defect.defectTracker.service.SeverityService;
import com.defect.defectTracker.utils.StandardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/severity")
public class SeverityController {

    Logger logger = LoggerFactory.getLogger(SeverityController.class);

    @Autowired
    private SeverityService severityService;

    @PostMapping
    public ResponseEntity<StandardResponse> createSeverity(@RequestBody SeverityDto dto) {
        logger.info("Creating severity with details: {}", dto);
        try {
            SeverityDto created = severityService.createSeverity(dto);
            logger.info("Severity created successfully with ID: {}", created.getId());

            StandardResponse response = new StandardResponse(

                    "201-Created",
                    "Severity created successfully",
                    created,
                    200
            );
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException ex) {
            logger.error("Error creating severity: {}", ex.getMessage(), ex);

            if (ex.getMessage() != null && ex.getMessage().contains("Severity name must be unique")) {
                logger.info("Severity name must be unique");
                StandardResponse response = new StandardResponse(
                        "400-Bad Request",
                        ex.getMessage(),
                        null,
                        400
                );
                return ResponseEntity.badRequest().body(response);
            }
            // Fallback for any other error
            StandardResponse response = new StandardResponse(
                    "500-Internal Server Error",
                    ex.getMessage(),
                    null,
                    500
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAllSeverities() {
        logger.info("Fetching all severities");
        List<SeverityDto> severities = severityService.getAllSeverities();

        StandardResponse response = new StandardResponse(
                "200-OK",
                "Fetched all severities",
                severities,
                200
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updateSeverity(
            @PathVariable Long id,
            @RequestBody SeverityDto severityDto) {
        logger.info("Updating severity with ID: {} and details: {}", id, severityDto);
        try {
            SeverityDto updated = severityService.updateSeverity(id, severityDto);
            if (updated != null) {
                StandardResponse response = new StandardResponse(
                        "200-OK",
                        "Severity updated successfully",
                        updated,
                        200
                );
                return ResponseEntity.ok(response);
            } else {
                StandardResponse response = new StandardResponse(
                        "404-Not Found",
                        "Severity not found",
                        null,
                        404
                );
                return ResponseEntity.status(404).body(response);
            }
        } catch (IllegalArgumentException ex) {
            logger.error("Error updating severity: {}", ex.getMessage(), ex);
            StandardResponse response = new StandardResponse(
                    "400-Bad Request",
                    ex.getMessage(),
                    null,
                    400
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deleteSeverity(@PathVariable Long id) {
        logger.info("Deleting severity with ID: {}", id);
        boolean deleted = severityService.deleteSeverity(id);
        if (deleted) {
            StandardResponse response = new StandardResponse(
                    "200-OK",
                    "Severity deleted successfully",
                    null,
                    200
            );
            return ResponseEntity.ok(response);
        } else {
            StandardResponse response = new StandardResponse(
                    "400-Bad Request",
                    "Severity deletion failed",
                    null,
                    400
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
}
