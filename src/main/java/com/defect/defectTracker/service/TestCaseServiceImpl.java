package com.defect.defectTracker.service;
import com.defect.defectTracker.repository.ProjectRepository;
import com.defect.defectTracker.dto.TestCaseDto;
import com.defect.defectTracker.dto.TestCaseResponseDto;
import com.defect.defectTracker.entity.TestCase;
import com.defect.defectTracker.repository.*;
import com.defect.defectTracker.utils.StandardResponse;
import com.defect.defectTracker.utils.Utils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestCaseServiceImpl implements TestCaseService {

    Logger logger = LoggerFactory.getLogger(TestCaseServiceImpl.class);

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModulesRepository modulesRepository;

    @Autowired
    private SubModuleRepository subModuleRepository;

    @Autowired
    private DefectTypeRepository defectTypeRepository;

    @Autowired
    private SeverityRepository severityRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public StandardResponse getTestCasesByProjectAndSubmodule(Long projectId, Long submoduleId) {
        logger.debug("Fetching test cases for projectId: {} and submoduleId: {}", projectId, submoduleId);
        List<TestCase> testCases = testCaseRepository.findByProjectIdAndSubmoduleId(projectId, submoduleId);

        if (testCases.isEmpty()) {
            logger.info("No test cases found for projectId: {} and submoduleId: {}", projectId, submoduleId);
            return new StandardResponse("success", "No test cases found for the given criteria", Collections.emptyList(), 2000);
        }

        logger.debug("Found {} test cases", String.valueOf(testCases.size()));
        List<TestCaseDto> testCaseDtos = testCases.stream()
                .map(testCase -> {
                    logger.trace("Mapping TestCase to TestCaseDto: {}", testCase);
                    return modelMapper.map(testCase, TestCaseDto.class);
                })
                .collect(Collectors.toList());

        return new StandardResponse("success", "Test cases retrieved successfully", testCaseDtos, 2000);
    }

    @Override
    public StandardResponse getTestCasesByFilters(Long projectId, Long moduleId, Long submoduleId, Long releaseId) {
        try {
            if (!Utils.idValidation(projectId)) {
                return new StandardResponse("Failure", "Project ID is required", null, HttpStatus.BAD_REQUEST.value());
            }
            if (!Utils.idValidation(moduleId)) {
                return new StandardResponse("Failure", "Module ID is required", null, HttpStatus.BAD_REQUEST.value());
            }
            if (!Utils.idValidation(submoduleId)) {
                return new StandardResponse("Failure", "Submodule ID is required", null, HttpStatus.BAD_REQUEST.value());
            }
            if (!Utils.idValidation(releaseId)) {
                return new StandardResponse("Failure", "Release ID is required", null, HttpStatus.BAD_REQUEST.value());
            }

            List<TestCase> testCases = testCaseRepository.findByProjectAndModuleAndSubmoduleAndRelease(
                    projectId, moduleId, submoduleId, releaseId);

            if (testCases.isEmpty()) {
                return new StandardResponse("Failure", "Data not found", null, HttpStatus.BAD_REQUEST.value());
            }

            List<TestCaseResponseDto> responseDtos = testCases.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return new StandardResponse("success", "Retrieved Successfully", responseDtos, HttpStatus.OK.value());
        } catch (Exception e) {
            return new StandardResponse("Failure", "Retrieved failed: " + e.getMessage(), null, HttpStatus.BAD_REQUEST.value());
        }
    }

    private TestCaseResponseDto convertToDto(TestCase testCase) {
        TestCaseResponseDto dto = new TestCaseResponseDto();
        dto.setId(testCase.getId());
        dto.setDescription(testCase.getDescription());
        dto.setSteps(testCase.getSteps());
        dto.setTestCaseId(testCase.getTestCaseId());
        dto.setModuleId(testCase.getModules().getId().toString());
        dto.setProjectId(testCase.getProject().getId().toString());
        dto.setSeverityId(testCase.getSeverity().getId().toString());
        dto.setSubModuleId(testCase.getSubModule().getId().toString());
        dto.setTypeId(testCase.getDefectType().getId());

        return dto;
    }
    private String generateTestCaseId() {

        Long maxId = testCaseRepository.findTopByOrderByIdDesc();
        long nextNumericId = (maxId == null) ? 1 : maxId + 1;
        return String.format("RE%04d", nextNumericId);
    }

    @Override
    public StandardResponse createTestCase(TestCaseDto tcdto) {
        logger.info("Creating test case with description: {}", tcdto.getDescription());
        try {
            if (tcdto.getDescription() == null || tcdto.getDescription().trim().isEmpty()) {
                logger.error("Description is mandatory");
                return new StandardResponse("Failure", "Description is mandatory", null, 4000);
            }
            if (tcdto.getModuleId() == null || tcdto.getSubModuleId() == null || tcdto.getProjectId() == null) {
                return new StandardResponse("Failure", "Module, SubModule, and Project IDs are mandatory", null, 4000);
            }

            TestCase testCase = new TestCase();
            testCase.setTestCaseId(generateTestCaseId());
            testCase.setDescription(tcdto.getDescription());
            testCase.setSteps(tcdto.getSteps());
            testCase.setModules(modulesRepository.findById(tcdto.getModuleId()).orElseThrow());
            testCase.setSubModule(subModuleRepository.findById(tcdto.getSubModuleId()).orElseThrow());
            testCase.setProject(projectRepository.findById(tcdto.getProjectId()).orElseThrow());
            testCase.setSeverity(severityRepository.findById(tcdto.getSeverityId()).orElseThrow());
            testCase.setDefectType(defectTypeRepository.findById(tcdto.getDefectTypeId()).orElseThrow());

            testCaseRepository.save(testCase);
            logger.info("Test case created successfully with ID: {}", testCase.getTestCaseId());
            return new StandardResponse("Success", "Created successfully", null, 2001);
        } catch (Exception e) {
            logger.error("Error creating test case: {}", e.getMessage());
            return new StandardResponse("Failure", "Creation failed", null, 4000);
        }
    }

    @Override
    public StandardResponse updateTestCase(Long id, TestCaseDto tcdto) {
        logger.info("Updating test case with ID: {}", id);
        try {
            TestCase testCase = testCaseRepository.findById(id).orElse(null);
            if (testCase == null) {
                logger.error("Test case not found with ID: {}", id);
                return new StandardResponse("Failure", "Test case not found", null, 4000);
            }

            if (tcdto.getDescription() == null || tcdto.getDescription().trim().isEmpty()) {
                return new StandardResponse("Failure", "Description is mandatory", null, 4000);
            }

            testCase.setDescription(tcdto.getDescription());
            testCase.setSteps(tcdto.getSteps());
            testCase.setModules(modulesRepository.findById(tcdto.getModuleId()).orElseThrow());
            testCase.setSubModule(subModuleRepository.findById(tcdto.getSubModuleId()).orElseThrow());
            testCase.setProject(projectRepository.findById(tcdto.getProjectId()).orElseThrow());
            testCase.setDefectType(defectTypeRepository.findById(tcdto.getDefectTypeId()).orElseThrow());
            testCase.setSeverity(severityRepository.findById(tcdto.getSeverityId()).orElseThrow());

            testCaseRepository.save(testCase);
            logger.info("Test case updated successfully with ID: {}", testCase.getTestCaseId());
            return new StandardResponse("Success", "Updated successfully", null, 2001);

        } catch (Exception e) {
            logger.error("Error updating test case: {}", e.getMessage());
            return new StandardResponse("Failure", "Update failed", null, 4000);
        }
    }

    @Override
    public StandardResponse deleteTestCase(Long id) {
        logger.info("Deleting test case with ID: {}", id);
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TestCase not found with id: " + id));

        testCase.setDescription(null);
        testCase.setSteps(null);
        testCase.setModules(null);
        testCase.setSubModule(null);
        testCase.setProject(null);
        testCase.setDefectType(null);
        testCase.setSeverity(null);

        testCaseRepository.delete(testCase);
        logger.info("Successfully deleted testcase with ID: {}", id);
        return new StandardResponse("Success", "Deleted successfully", null, 2001);
    }

    @Override
    public StandardResponse searchTestCases(String description, Long typeId, Long severityId, Long submoduleId) {
        logger.debug("Searching test cases with parameters - description: {}, typeId: {}, severityId: {}, submoduleId: {}",
                description, typeId, severityId, submoduleId);

        List<TestCase> testCases = testCaseRepository.searchTestCases(description, typeId, severityId, submoduleId);

        if (testCases.isEmpty()) {
            logger.info("No test cases found matching search criteria");
            return new StandardResponse("success", "No test cases match the search criteria", Collections.emptyList(), 2000);
        }

        logger.debug("Found {} matching test cases", String.valueOf(testCases.size()));
        List<TestCaseDto> testCaseDtos = testCases.stream()
                .map(testCase -> {
                    logger.trace("Mapping TestCase to TestCaseDto: {}", testCase);
                    return modelMapper.map(testCase, TestCaseDto.class);
                })
                .collect(Collectors.toList());

        return new StandardResponse("success", "Test cases retrieved successfully", testCaseDtos, 2000);
    }

    @Override
    public TestCaseDto getById(Long id) {
        TestCase testCase = testCaseRepository.findById(id).orElse(null);
        if (testCase == null) return null;
        return modelMapper.map(testCase, TestCaseDto.class);
    }

    @Override
    public StandardResponse getTestCasesByProjectAndModule(Long projectId, Long moduleId) {
        List<TestCase> testCases = testCaseRepository.findByProjectIdAndModulesId(projectId, moduleId);
        if (testCases.isEmpty()) {
            return new StandardResponse("success", "No test cases found for the given criteria", Collections.emptyList(), 2000);
        }
        List<TestCaseDto> testCaseDtos = testCases.stream()
                .map(testCase -> modelMapper.map(testCase, TestCaseDto.class))
                .collect(Collectors.toList());
        return new StandardResponse("success", "Test cases retrieved successfully", testCaseDtos, 2000);
    }
}