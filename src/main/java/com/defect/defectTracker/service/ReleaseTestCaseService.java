package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ReleaseTestCaseDto;
import com.defect.defectTracker.dto.TestCaseAllocationDto;
import com.defect.defectTracker.utils.StandardResponse;

public interface ReleaseTestCaseService {
    boolean updateReleaseTestCaseStatus(Long id,ReleaseTestCaseDto releaseTestCaseDto);
    boolean releaseTestCaseExists(Long id);
    ReleaseTestCaseDto getById(Long id);
    ReleaseTestCaseDto updateReleaseTestCase(ReleaseTestCaseDto releaseTestCaseDto, int statusCode);
    StandardResponse allocateTestCasesToRelease(TestCaseAllocationDto request);

}
