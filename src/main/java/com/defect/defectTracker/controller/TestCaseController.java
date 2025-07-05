package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.TestCaseAllocationDto;
import com.defect.defectTracker.dto.TestCaseDto;
import com.defect.defectTracker.service.ReleaseTestCaseService;
import com.defect.defectTracker.service.TestCaseService;
import com.defect.defectTracker.utils.StandardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/testcase")
public class TestCaseController {

    private static final Logger logger = LoggerFactory.getLogger(TestCaseController.class);

    @Autowired
    private TestCaseService testCaseService;

    // Filter test cases by projectId and submoduleId
    @GetMapping("/filter")
    public ResponseEntity<StandardResponse> getTestCasesByProjectAndSubmodule(
            @RequestParam Long projectId,
            @RequestParam Long submoduleId) {
        logger.info("Received request to filter test cases for projectId: {} and submoduleId: {}", projectId, submoduleId);
        StandardResponse response = testCaseService.getTestCasesByProjectAndSubmodule(projectId, submoduleId);
        logger.debug("Filter response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Filter by project, module, submodule and release
    @GetMapping("/filters")
    public ResponseEntity<StandardResponse> getTestCasesByFilters(
            @RequestParam Long projectId,
            @RequestParam Long moduleId,
            @RequestParam Long submoduleId,
            @RequestParam Long releaseId) {
        StandardResponse response = testCaseService.getTestCasesByFilters(projectId, moduleId, submoduleId, releaseId);
        return ResponseEntity.ok(response);
    }

    // Create a test case
    @PostMapping
    public ResponseEntity<StandardResponse> create(@RequestBody TestCaseDto dto) {
        StandardResponse response = testCaseService.createTestCase(dto);
        return ResponseEntity.ok(response);
    }

    // Update a test case
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> update(@PathVariable Long id, @RequestBody TestCaseDto dto) {
        StandardResponse response = testCaseService.updateTestCase(id, dto);
        return ResponseEntity.ok(response);
    }

    // Delete a test case
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deleteTestCase(@PathVariable Long id) {
        StandardResponse response = testCaseService.deleteTestCase(id);
        return ResponseEntity.ok(response);
    }

    // Search test cases with optional parameters
    @GetMapping("/search")
    public ResponseEntity<StandardResponse> searchTestCases(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long severityId,
            @RequestParam(required = false) Long submoduleId) {
        logger.info("Received search request with parameters - description: {}, typeId: {}, severityId: {}, submoduleId: {}",
                description, typeId, severityId, submoduleId);
        StandardResponse response = testCaseService.searchTestCases(description, typeId, severityId, submoduleId);
        logger.debug("Search response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/filterbymodule")
    public ResponseEntity<StandardResponse> getTestCasesByProjectAndModule(
            @RequestParam Long projectId,
            @RequestParam Long moduleId) {
        logger.info("Received request to filter test cases for projectId: {} and moduleId: {}", projectId, moduleId);
        StandardResponse response = testCaseService.getTestCasesByProjectAndModule(projectId, moduleId);
        logger.debug("filterbymodule response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




}