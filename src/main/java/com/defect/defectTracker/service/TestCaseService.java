package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.TestCaseDto;
import com.defect.defectTracker.utils.StandardResponse;

public interface TestCaseService {
    TestCaseDto getById(Long id);

    StandardResponse getTestCasesByProjectAndSubmodule(Long projectId, Long submoduleId);

    StandardResponse getTestCasesByFilters(Long projectId, Long moduleId, Long submoduleId, Long releaseId);

    StandardResponse createTestCase(TestCaseDto tcdto);
    StandardResponse updateTestCase(Long id, TestCaseDto tcdto);
    StandardResponse deleteTestCase(Long id);

    StandardResponse searchTestCases(String description, Long typeId, Long severityId, Long submoduleId);
    StandardResponse getTestCasesByProjectAndModule(Long projectId, Long moduleId);
}
