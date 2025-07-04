package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.DefectDto;
import com.defect.defectTracker.service.DefectService;
import com.defect.defectTracker.utils.StandardResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.io.IOException;
import com.defect.defectTracker.utils.StandardResponse;

import java.io.IOException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/defect")
@RequiredArgsConstructor

public class DefectController {

    Logger logger = LoggerFactory.getLogger(DefectController.class);

    @Autowired
    private DefectService defectService;

    @PostMapping
    public ResponseEntity<StandardResponse> createDefect(@RequestBody DefectDto defectDto) {
        try {
            StandardResponse response = defectService.createDefect(defectDto);
            return ResponseEntity
                    .status(response.getStatusCode() == 2000 ? 201 : 400)
                    .body(response);
        } catch (Exception e) {
            // Optionally log the error here
            e.printStackTrace(); // or use a logger
            StandardResponse errorResponse = new StandardResponse(
                    "Error",
                    "Failed to create defect: " + e.getMessage(),
                    null,
                    500
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getDefectById(@PathVariable Long id) {
        try {
            DefectDto defectDto = defectService.getDefectById(id);
            return ResponseEntity.ok(
                    new StandardResponse("success", "defect found for this id", defectDto, 2000)
            );
        } catch (Exception e) {
            return ResponseEntity.status(404).body(
                    new StandardResponse("failure", "No defect found for this id", null, 4000)
            );
        }
    }
    //i am writing this code to get the defect by id
    //No get output is coming from the service layer
//    @GetMapping("/{id}")
//    public ResponseEntity<StandardResponse> getDefectById(@PathVariable Long id) {
//        try {
//            DefectDto defectDto = defectService.getById(id);
//            return ResponseEntity.ok(
//                    new StandardResponse("success", "defect found for this id", defectDto, 2000)
//            );
//        } catch (Exception e) {
//            return ResponseEntity.status(404).body(
//                    new StandardResponse("failure", "No defect found for this id", null, 4000)
//            );
//        }
//    }
    //03----------------------------------------------------------------------------------------------------------------

    @PutMapping("{id}")
    public ResponseEntity<StandardResponse> updateDefect(@PathVariable Long id, @RequestBody DefectDto dto) {
        StandardResponse response = defectService.updateDefect(id, dto);
        int statusCode = response.getStatusCode() == 2000 ? 200 : 400;
        return ResponseEntity.status(statusCode).body(response);}
    //04----------------------------------------------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deleteDefect(@PathVariable Long id) {
        StandardResponse response = defectService.deleteDefectById(id);
        return ResponseEntity.ok(response);
    }
    //05----------------------------------------------------------------------------------------------------------------
    @GetMapping("/project/{projectId}")
    public ResponseEntity<StandardResponse> getDefectsByProjectId (@PathVariable Long projectId){
        try {
            List<DefectDto> defectList = defectService.getDefectsByProjectId(projectId);
            return ResponseEntity.ok(
                    new StandardResponse("success", "defect found for this id", defectList, 2000)
            );
        } catch (Exception e) {
            return ResponseEntity.status(404).body(
                    new StandardResponse("failure", "No defect found for this projectId", null, 4000)
            );
        }
    }
    //06----------------------------------------------------------------------------------------------------------------
    @GetMapping("/byReleaseTestcase/{releaseTestcaseId}")
    public ResponseEntity<StandardResponse> getDefectsByReleaseTestCaseId (@PathVariable Long releaseTestcaseId)
    {
        try {
            DefectDto defects = defectService.getDefectsByReleaseTestCaseId(releaseTestcaseId);
            return ResponseEntity.ok(new StandardResponse(
                    "Success", "Defects fetched successfully", defects, 2001
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(new StandardResponse(
                    "Failure", e.getMessage(), null, 4004
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new StandardResponse(
                    "Failure", "Internal Server Error", null, 5000
            ));
        }
    }
    //i am writing this code to get the defect by id
    //No get output is coming from the service layer
//    @GetMapping("/byReleaseTestcase/{releaseTestcaseId}")
//    public ResponseEntity<StandardResponse> getDefectsByReleaseTestCaseId (@PathVariable Long releaseTestcaseId)
//    {
//        try {
//            DefectDto defects = defectService.getByReleaseTestCaseId(releaseTestcaseId);
//            return ResponseEntity.ok(new StandardResponse(
//                    "Success", "Defects fetched successfully", defects, 2001
//            ));
//        } catch (NoSuchElementException e) {
//            return ResponseEntity.status(404).body(new StandardResponse(
//                    "Failure", e.getMessage(), null, 4004
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(new StandardResponse(
//                    "Failure", "Internal Server Error", null, 5000
//            ));
//        }
//    }

    //07----------------------------------------------------------------------------------------------------------------
    @GetMapping("/filter")
    public ResponseEntity<StandardResponse> filterDefects (
            @RequestParam Long projectId,
            @RequestParam(required = false) Long defectStatusId,
            @RequestParam(required = false) Long severityId,
            @RequestParam(required = false) Long priorityId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long ReleaseTestCaseId,
            @RequestParam(required = false) Long AssignbyId,
            @RequestParam(required = false) Long AssigntoId

    ){
        DefectDto dto = new DefectDto();
        dto.setProjectId(projectId);
        dto.setDefectStatusId(defectStatusId);
        dto.setSeverityId(severityId);
        dto.setPriorityId(priorityId);
        dto.setTypeId(typeId);
        dto.setReleaseTestCaseId(ReleaseTestCaseId);
        dto.setAssignbyId(AssignbyId);
        dto.setAssigntoId(AssigntoId);
        return defectService.filterDefects(dto);
    }
    //08----------------------------------------------------------------------------------------------------------------

    @GetMapping("/export")
    public void exportDefects (HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=defects.csv");
        defectService.exportDefects(response);
    }


    //------------------------------------------------------------------------------------------------------------------
    @GetMapping("/exportAsId")
    public void exportdefIds(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=defects.csv");
        defectService.exportdefectIds(response);
    }
    //----------------------------------------------------------------------------------------------------------------------
    @PostMapping(value = "/import", consumes = {"multipart/form-data"})
    public ResponseEntity<StandardResponse> uploadDefects(@RequestParam("file") MultipartFile file) {
        try {
            int importedCount = defectService.uploadDefects(file);
            String msg = "Imported Successfully: " + importedCount + " records";
            StandardResponse response = new StandardResponse("success", "Imported Successfully", null, 2000);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Import failed", e);
            StandardResponse errorResponse = new StandardResponse("failure",  "Import failed: " + e.getMessage(),null, 4000);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
//---