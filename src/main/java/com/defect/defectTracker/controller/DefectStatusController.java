package com.defect.defectTracker.controller;


import com.defect.defectTracker.dto.DefectStatusDto;
import com.defect.defectTracker.service.DefectStatusService;
import com.defect.defectTracker.utils.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/defectStatus")
@RequiredArgsConstructor
public class DefectStatusController {

  Logger logger = LoggerFactory.getLogger(DefectStatusController.class);

    @Autowired
    private  DefectStatusService service;

    @PostMapping
    public ResponseEntity<StandardResponse> createDefectStatus(@RequestBody DefectStatusDto dto) {
        logger.info("create defect status with details: {}", dto);

        DefectStatusDto created = service.createDefectStatus(dto);
        if (created == null) {
            return new ResponseEntity<>(new StandardResponse("error", "Invalid or duplicate defect status", null, 400), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new StandardResponse("success", "Created successfully", created, 200), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAll() {
        List<DefectStatusDto> list = service.getAllDefectStatuses();
        return new ResponseEntity<>(new StandardResponse("success", "All defect statuses", list, 200), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getById(@PathVariable Long id) {
        DefectStatusDto dto = service.getDefectStatusById(id);
        return new ResponseEntity<>(new StandardResponse("success", "Defect status found", dto, 200), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> update(@PathVariable Long id, @RequestBody DefectStatusDto dto) {
        DefectStatusDto updated = service.updateDefectStatus(id, dto);
        return new ResponseEntity<>(new StandardResponse("success", "Updated successfully", updated, 200), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> delete(@PathVariable Long id) {
        service.deleteDefectStatus(id);
        return new ResponseEntity<>(new StandardResponse("success", "Deleted successfully", null, 200), HttpStatus.OK);
    }
}
