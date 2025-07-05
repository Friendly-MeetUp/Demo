package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.SubModuleDto;
import com.defect.defectTracker.service.SubModuleService;
import com.defect.defectTracker.utils.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subModule")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubModuleController {

    private final SubModuleService subModuleService;

    @PostMapping
    public ResponseEntity<StandardResponse> createSubModule(@RequestBody SubModuleDto dto) {
        try {
            subModuleService.createSubModule(dto);
            return new ResponseEntity<>(new StandardResponse("success", "Created successfully", null, 2000), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StandardResponse("failure", e.getMessage(), null, 4000), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<StandardResponse> getSubModulesByModuleId(@PathVariable Long moduleId) {
        try {
            List<SubModuleDto> subModules = subModuleService.getSubModulesByModuleId(moduleId);
            if (subModules.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new StandardResponse("failure", "No submodules found for this module", null, 4000)
                );
            }
            return ResponseEntity.ok(
                    new StandardResponse("success", "Retrieved successfully", subModules, 2000)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse("failure", e.getMessage(), null, 4000)
            );
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deleteSubModule(@PathVariable Long id) {
        try {
            subModuleService.deleteSubModuleBySubModuleId(id);
            return new ResponseEntity<>(new StandardResponse("success", "Deleted successfully", null, 2000), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StandardResponse("failure", e.getMessage(), null, 4000), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updateSubModule(@PathVariable Long id, @RequestBody SubModuleDto dto) {
        try {
            subModuleService.updateSubModule(id, dto);
            return new ResponseEntity<>(new StandardResponse("success", "Updated successfully", null, 2000), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new StandardResponse("info", e.getMessage(), null, 3000), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StandardResponse("failure", "Data not found or invalid data.", null, 4000), HttpStatus.BAD_REQUEST);
        }
    }

}