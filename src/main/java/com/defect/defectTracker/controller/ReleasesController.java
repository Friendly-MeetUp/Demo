package com.defect.defectTracker.controller;


import com.defect.defectTracker.dto.ReleasesDto;
import com.defect.defectTracker.entity.Releases;
import com.defect.defectTracker.service.ReleasesService;
import com.defect.defectTracker.utils.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/releases")
public class ReleasesController {

    @Autowired
    private ReleasesService releasesService;

    @PostMapping()
    public ResponseEntity<StandardResponse> createRelease(@RequestBody ReleasesDto releaseDto) {
        try {
        Releases savedReleases = releasesService.createRelease(releaseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StandardResponse(
                "success",
                "Release created successfully",
                null,
                2000
        ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StandardResponse(
                    "error",
                    "Failed to create release: " + e.getMessage(),
                    null,
                    4000
            ));
        }
    }


    @GetMapping("/project/{projectId}")
    public ResponseEntity<StandardResponse> getReleasesByProjectId(@PathVariable Long projectId) {
        try {
            List<ReleasesDto> releases = releasesService.getReleasesByProjectId(projectId);

            if (releases == null || releases.isEmpty()) {
                return ResponseEntity.ok(new StandardResponse(
                        "success",
                        "No releases found for the specified project",
                        Collections.emptyList(),
                        2000
                ));
            }

            return ResponseEntity.ok(new StandardResponse(
                    "success",
                    "Releases retrieved successfully",
                    releases,
                    2000
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StandardResponse(
                    "error",
                    e.getMessage(),
                    null,
                    4000
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StandardResponse(
                    "error",
                    "Failed to retrieve releases: " + e.getMessage(),
                    null,
                    4000
            ));
        }
    }


//    @GetMapping("/search")
//    public ResponseEntity<StandardResponse> searchReleases(
//            @RequestParam(required = false) Long projectId,
//            @RequestParam(required = false) String releaseName) {
//        try {
//            // Validate at least one search parameter is provided
//            if ((releaseName == null || releaseName.trim().isEmpty()) && projectId == null) {
//                return ResponseEntity.badRequest().body(new StandardResponse(
//                        "error",
//                        "At least one search parameter (projectId or releaseName) is required",
//                        null,
//                        4000
//                ));
//            }
//
//            List<ReleasesDto> releases;
//
//            if (projectId != null && releaseName != null && !releaseName.trim().isEmpty()) {
//
//                releases = releasesService.searchByProjectAndName(projectId, releaseName.trim());
//            } else if (projectId != null) {
//
//                releases = releasesService.searchByProjectId(projectId);
//            } else {
//
//                releases = releasesService.searchByReleaseName(releaseName.trim());
//            }
//
//            if (releases.isEmpty()) {
//                String message;
//                if (projectId != null && releaseName != null) {
//                    message = String.format("No releases found for project %d with name containing '%s'",
//                            projectId, releaseName);
//                } else if (projectId != null) {
//                    message = String.format("No releases found for project %d", projectId);
//                } else {
//                    message = String.format("No releases found with name containing '%s'", releaseName);
//                }
//
//                return ResponseEntity.ok(new StandardResponse(
//                        "success",
//                        message,
//                        Collections.emptyList(),
//                        2000
//                ));
//            }
//
//            String successMessage;
//            if (projectId != null && releaseName != null) {
//                successMessage = String.format("Releases found for project %d with name containing '%s'",
//                        projectId, releaseName);
//            } else if (projectId != null) {
//                successMessage = String.format("Releases found for project %d", projectId);
//            } else {
//                successMessage = String.format("Releases found with name containing '%s'", releaseName);
//            }
//
//            return ResponseEntity.ok(new StandardResponse(
//                    "success",
//                    successMessage,
//                    releases,
//                    2000
//            ));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(new StandardResponse(
//                    "error",
//                    e.getMessage(),
//                    null,
//                    4000
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StandardResponse(
//                    "error",
//                    "Failed to search releases: " + e.getMessage(),
//                    null,
//                    5000
//            ));
//        }
//    }

}



