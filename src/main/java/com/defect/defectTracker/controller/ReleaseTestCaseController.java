package com.defect.defectTracker.controller;


import com.defect.defectTracker.dto.DefectDto;
import com.defect.defectTracker.dto.ReleaseTestCaseDto;
import com.defect.defectTracker.dto.TestCaseAllocationDto;
import com.defect.defectTracker.service.DefectService;
import com.defect.defectTracker.service.ReleaseTestCaseService;
import com.defect.defectTracker.utils.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/releasetestcase")
@RequiredArgsConstructor
public class ReleaseTestCaseController {
    Logger logger = LoggerFactory.getLogger(ReleaseTestCaseController.class);
    private final ReleaseTestCaseService releaseTestCaseService;
    private final DefectService defectService;
    private final ModelMapper modelMapper;

    @PostMapping("/allocate")
    public ResponseEntity<StandardResponse> allocateTestCasesToRelease(
            @RequestBody TestCaseAllocationDto request) {
        StandardResponse response = releaseTestCaseService.allocateTestCasesToRelease(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<StandardResponse> updateReleaseTestCaseStatus(@PathVariable("id") Long releaseId, @RequestBody ReleaseTestCaseDto releaseTestCaseDto) {
        logger.info("Update Release Test Case Status started");
        int prev_status = releaseTestCaseDto.getTestCaseStatus();
        Long priorityId = releaseTestCaseDto.getPriorityId();
        Long defectStatusId = releaseTestCaseDto.getDefectStatusId();
        logger.info("Release Test Case ID: {}", releaseId);
        //logger.info("Release Test Case Status: {}", releaseTestCaseDto.getTestCaseStatus());
        releaseTestCaseDto = releaseTestCaseService.getById(releaseId);
        releaseTestCaseDto.setTestCaseStatus(prev_status);
        releaseTestCaseDto.setPriorityId(priorityId);
        releaseTestCaseDto.setDefectStatusId(defectStatusId);
        if (!releaseTestCaseService.releaseTestCaseExists(releaseId)) {
            logger.info("Update Release Test Case Status failed: Release Test Case does not exist");
            return ResponseEntity.ok(new StandardResponse("Failure", "Release Test Case does not exist", null, 4000));
        }
        if (releaseTestCaseService.updateReleaseTestCaseStatus(releaseId, releaseTestCaseDto)) {
            return ResponseEntity.ok(new StandardResponse("Success", "Updated Successfully", null, 2000));
        } else {
            return ResponseEntity.ok(new StandardResponse("Failure", "Update Failed", null, 4000));
        }
    }
}
