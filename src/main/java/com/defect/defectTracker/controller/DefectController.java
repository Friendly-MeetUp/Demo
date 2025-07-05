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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/defect")
@RequiredArgsConstructor

public class DefectController {

    Logger logger = LoggerFactory.getLogger(DefectController.class);

    @Autowired
    private DefectService defectService;
//Post Controller
    @PostMapping
    public ResponseEntity<StandardResponse> createDefect(@RequestBody DefectDto defectDto) {
        try {
            StandardResponse response = defectService.createDefect(defectDto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // or use a logger
            StandardResponse errorResponse = new StandardResponse(
                    "Failure",
                    "Failed to create defect: " + e.getMessage(),
                    null,
                    4000
            );
            return ResponseEntity.ok(errorResponse);        }
    }
//Update Controller
@PutMapping("/{id}")
public ResponseEntity<StandardResponse> updateDefect(@PathVariable Long id, @RequestBody DefectDto dto) {
    try {
        StandardResponse response = defectService.updateDefect(id, dto);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        e.printStackTrace(); // Or use a proper logger
        StandardResponse errorResponse = new StandardResponse(
                "Failure",
                "Failed to update defect: " + e.getMessage(),
                null,
                4001
        );
        return ResponseEntity.ok().body(errorResponse);
    }
}


//Update Controller
@GetMapping("/{id}")
public ResponseEntity<StandardResponse> getDefectById(@PathVariable Long id) {
    try {
        Map<String, Object> defectMap = defectService.getDefectCustomResponseById(id);
        return ResponseEntity.ok(
                new StandardResponse("success", "defect found for this id", defectMap, 2000)
        );
    } catch (NoSuchElementException e) {
        return ResponseEntity.ok().body(
                new StandardResponse("failure", e.getMessage(), null, 4000)
        );
    } catch (Exception e) {
        return ResponseEntity.ok().body(
                new StandardResponse("error", "Internal server error", null, 4000)
        );
    }
}

    //05----------------------------------------------------------------------------------------------------------------
    @GetMapping("/project/{projectId}")
    public ResponseEntity<StandardResponse> getDefectsByProjectId(@PathVariable Long projectId) {
        try {
            List<Map<String, Object>> defectList = defectService.getDefectsCustomResponseByProjectId(projectId);
            if (defectList.isEmpty()) {
                return ResponseEntity.ok().body(
                        new StandardResponse("failure", "No defects found for this projectId", null, 4000)
                );
            }
            return ResponseEntity.ok(
                    new StandardResponse("success", "defects found for this projectId", defectList, 2000)
            );
        } catch (Exception e) {
            return ResponseEntity.ok().body(
                    new StandardResponse("error", "Internal server error", null, 4000)
            );
        }
    }

    //06----------------------------------------------------------------------------------------------------------------
    @GetMapping("/byReleaseTestcase/{releaseTestcaseId}")
    public ResponseEntity<StandardResponse> getDefectsByReleaseTestCaseId(@PathVariable Long releaseTestcaseId) {
        try {
            Map<String, Object> defectMap = defectService.getDefectCustomResponseByReleaseTestCaseId(releaseTestcaseId);
            return ResponseEntity.ok(new StandardResponse(
                    "Success", "Defect fetched successfully", defectMap, 2000
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok().body(new StandardResponse(
                    "Failure", e.getMessage(), null, 4000
            ));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new StandardResponse(
                    "Failure", "Internal Server Error", null, 4000
            ));
        }
    }

    //07----------------------------------------------------------------------------------------------------------------
    @GetMapping("/byReleaseTestcase/testcase/{testCaseId}")
    public ResponseEntity<StandardResponse> fetchDefectsByTestCaseIdWithDetails(@PathVariable Long testCaseId) {
        try {
            List<Map<String, Object>> defectList = defectService.fetchDefectsByTestCaseIdWithDetails(testCaseId);
            return ResponseEntity.ok(new StandardResponse(
                    "Success", "Defects fetched successfully", defectList, 2000
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok(new StandardResponse(
                    "Failure", e.getMessage(), null, 4000
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new StandardResponse(
                    "Failure", "Internal Server Error", null, 4000
            ));
        }
    }
//    ----------------------------------------------------------------------------
@GetMapping("/filter")
public ResponseEntity<StandardResponse> filterDefects(
        @RequestParam(required = false) Long projectId,
        @RequestParam(required = false) Long defectStatusId,
        @RequestParam(required = false) Long severityId,
        @RequestParam(required = false) Long priorityId,
        @RequestParam(required = false) Long typeId,
        @RequestParam(name = "releaseTestCaseId", required = false) Long releaseTestCaseId,
        @RequestParam(name = "assignById", required = false) Long assignById,
        @RequestParam(name = "assignToId", required = false) Long assignToId,
        @RequestParam(required = false) Long moduleId,
        @RequestParam(required = false) Long subModuleId
) {
    try {
        // Check required param
        if (projectId == null) {
            return ResponseEntity.ok(new StandardResponse(
                    "Failure", "Project ID is mandatory", null, 4000
            ));
        }

        DefectDto dto = new DefectDto();
        dto.setProjectId(projectId);
        dto.setDefectStatusId(defectStatusId);
        dto.setSeverityId(severityId);
        dto.setPriorityId(priorityId);
        dto.setTypeId(typeId);
        dto.setReleaseTestCaseId(releaseTestCaseId);
        dto.setAssignbyId(assignById);
        dto.setAssigntoId(assignToId);
        dto.setModuleId(moduleId);
        dto.setSubModuleId(subModuleId);

        // Delegate to service
        return defectService.filterDefects(dto);

    } catch (Exception e) {
        return ResponseEntity.ok(new StandardResponse(
                "Failure", "Internal Server Error", null, 4000
        ));
    }
}
    //08----------------------------------------------------------------------------------------------------------------

    @GetMapping("/export")
    public void exportDefects (HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=defects.csv");
        defectService.exportDefects(response);
    }

    //----------------------------------------------------------------------------------------------------------------------
    @PostMapping("/import")
    public ResponseEntity<StandardResponse> importDefects(@RequestParam("file") MultipartFile file) {
        try {
            int count = defectService.importDefectsFromCsv(file);

            if (count == 0) {
                return ResponseEntity.ok(new StandardResponse(
                        "failure",
                        "Imported 0 defects",
                        null,
                        4000
                ));
            } else {
                return ResponseEntity.ok(new StandardResponse(
                        "success",
                        "Imported " + count + " defects",
                        null,
                        2000
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(
                    new StandardResponse(
                            "error",
                            "Import failed: " + e.getMessage(),
                            null,
                            4000
                    )
            );
        }
    }


    //Delete Controller
@DeleteMapping("/{id}")
public ResponseEntity<StandardResponse> deleteDefect(@PathVariable Long id) {
    try {
        StandardResponse response = defectService.deleteDefectById(id);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        e.printStackTrace(); // You can replace this with proper logging
        StandardResponse errorResponse = new StandardResponse(
                "Failure",
                "Failed to delete defect: " + e.getMessage(),
                null,
                4000
        );
        return ResponseEntity.ok(errorResponse);
    }
}
}