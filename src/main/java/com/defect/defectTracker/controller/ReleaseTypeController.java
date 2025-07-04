package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.ReleaseTypeDto;
import com.defect.defectTracker.service.ReleaseTypeService;
import com.defect.defectTracker.utils.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/ReleaseType")
@RequiredArgsConstructor
public class ReleaseTypeController {

    @Autowired
    private  ReleaseTypeService service;

    @PostMapping
    public ResponseEntity<StandardResponse> create(@RequestBody ReleaseTypeDto dto) {
        try {
            ReleaseTypeDto created = service.createReleaseType(dto);
            return ResponseEntity.ok(new StandardResponse("success", "Created successfully", created, 2000));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StandardResponse("error", e.getMessage(), null, 4000));
        }
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAll() {
        List<ReleaseTypeDto> list = service.getAllReleaseTypes();
        return ResponseEntity.ok(new StandardResponse("success", "Fetched all records", list, 2000));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getById(@PathVariable Long id) {
        try {
            ReleaseTypeDto dto = service.getReleaseTypeById(id);
            return ResponseEntity.ok(new StandardResponse("success", "Found", dto, 2000));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StandardResponse("error", e.getMessage(), null, 4000));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> update(@PathVariable Long id, @RequestBody ReleaseTypeDto dto) {
        try {
            ReleaseTypeDto updated = service.updateReleaseType(id, dto);
            return ResponseEntity.ok(new StandardResponse("success", "Updated successfully", updated, 2000));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StandardResponse("error", e.getMessage(), null, 4000));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> delete(@PathVariable Long id) {
        if (service.deleteReleaseType(id)) {
            return ResponseEntity.ok(new StandardResponse("success", "Delete successfully", null, 2000));
        } else {
            return ResponseEntity.badRequest().body(new StandardResponse("error", "Delete Failed", null, 4000));
        }
    }
}
