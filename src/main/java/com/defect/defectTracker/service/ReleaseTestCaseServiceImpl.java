package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.DefectDto;
import com.defect.defectTracker.dto.ReleaseTestCaseDto;
import com.defect.defectTracker.dto.TestCaseAllocationDto;
import com.defect.defectTracker.entity.Defect;
import com.defect.defectTracker.entity.ReleaseTestCase;
import com.defect.defectTracker.entity.Releases;
import com.defect.defectTracker.entity.TestCase;
import com.defect.defectTracker.repository.DefectRepository;
import com.defect.defectTracker.repository.ReleaseTestCaseRepository;
import com.defect.defectTracker.repository.ReleasesRepository;
import com.defect.defectTracker.repository.TestCaseRepository;
import com.defect.defectTracker.utils.StandardResponse;
import com.defect.defectTracker.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReleaseTestCaseServiceImpl implements ReleaseTestCaseService {
    Logger logger = LoggerFactory.getLogger(ReleaseTestCaseServiceImpl.class);

    @Autowired
    private DefectService defectService;
    @Autowired
    private ReleaseTestCaseRepository releaseTestCaseRepository;
    @Autowired
    private TestCaseRepository testCaseRepository;
    @Autowired
    private DefectRepository defectRepository;
    @Autowired
    private ReleasesRepository releasesRepository;



        @Override
        @Transactional
        public StandardResponse allocateTestCasesToRelease(TestCaseAllocationDto request) {
            try {
                // Validate request
                if (request.getTestCaseIds() == null || request.getTestCaseIds().isEmpty()) {
                    return new StandardResponse("Failure", "Test case IDs cannot be empty", null, HttpStatus.BAD_REQUEST.value());
                }

                if (!Utils.idValidation(request.getReleaseId())) {
                    return new StandardResponse("Failure", "Release ID is invalid", null, HttpStatus.BAD_REQUEST.value());
                }

                // Check if release exists
                Releases release = releasesRepository.findById(request.getReleaseId())
                        .orElseThrow(() -> new RuntimeException("Release not found"));

                // Get all test cases to be allocated
                List<TestCase> testCases = testCaseRepository.findByIdIn(request.getTestCaseIds());

                if (testCases.isEmpty()) {
                    return new StandardResponse("Failure", "No valid test cases found for allocation", null, HttpStatus.BAD_REQUEST.value());
                }

                int allocatedCount = 0;
                int failedCount = 0;

                // Allocate each test case to the release
                for (TestCase testCase : testCases) {
                    try {
                        // Check if this test case is already allocated to this release
                        ReleaseTestCase releaseTestCase = releaseTestCaseRepository.findByReleasesIdAndTestCaseId(
                                release.getId(), testCase.getId());

                        if (releaseTestCase == null) {
                            // Not allocated yet, create new
                            releaseTestCase = new ReleaseTestCase();
                            releaseTestCase.setReleaseTestCaseId(UUID.randomUUID().toString());
                            releaseTestCase.setTestCase(testCase);
                            releaseTestCase.setReleases(release);
                        }
                        // Always update allocation date/time and status
                        releaseTestCase.setTestDate(new Date());
                        releaseTestCase.setTestTime(new Time(System.currentTimeMillis()));
                        releaseTestCase.setTestCaseStatus(0); // Default status as int (e.g., 0 for Pending)

                        releaseTestCaseRepository.save(releaseTestCase);
                        allocatedCount++;
                    } catch (Exception e) {
                        failedCount++;
                        // Log the error if needed
                    }
                }

                // Prepare response
                TestCaseAllocationDto response = new TestCaseAllocationDto();
                response.setAllocatedCount(allocatedCount);
                response.setFailedCount(failedCount);
                response.setReleaseId(release.getId());

                if (allocatedCount == 0) {
                    return new StandardResponse("Failure", "No test cases were allocated", response, HttpStatus.BAD_REQUEST.value());
                }

                return new StandardResponse("success", "Allocation Successful", response, HttpStatus.OK.value());

            } catch (RuntimeException e) {
                return new StandardResponse("Failure", e.getMessage(), null, HttpStatus.BAD_REQUEST.value());
            } catch (Exception e) {
                return new StandardResponse("Failure", "Allocation failed", null, HttpStatus.BAD_REQUEST.value());
            }
        }



    @Override
    public boolean updateReleaseTestCaseStatus(Long id, ReleaseTestCaseDto releaseTestCaseDto) {
        int status = releaseTestCaseDto.getTestCaseStatus();
        Optional<ReleaseTestCase> releaseTestCaseUpdate = releaseTestCaseRepository.findById(id);
        if (releaseTestCaseUpdate.isEmpty()) {
            logger.error("Release test case with ID {} not found", id);
            return false;
        }
        ReleaseTestCase releaseTestCase = releaseTestCaseUpdate.get();
        releaseTestCase.setTestCaseStatus(status);
        releaseTestCaseRepository.save(releaseTestCase);
        if (status == 0) { // Failed
            boolean defectExist = defectRepository.existsByReleaseTestCaseId(id);
            if (defectExist) {
                Defect defect = defectRepository.findByReleaseTestCaseId(id);
                int reOpenedCount = defect.getReOpenCount() + 1;
                defect.setReOpenCount(reOpenedCount);
                defectRepository.save(defect);
                return true;
            } else {
                Long testCaseId = releaseTestCase.getTestCase().getId();
                Optional<TestCase> testCaseOpt = testCaseRepository.findById(testCaseId);
                if (testCaseOpt.isPresent()) {
                    TestCase testCase = testCaseOpt.get();
                    DefectDto defectDto = new DefectDto();
                    BeanUtils.copyProperties(testCase, defectDto);
                    defectDto.setReleaseTestCaseId(releaseTestCase.getId());
                    defectDto.setTypeId(testCase.getDefectType().getId());
                    defectDto.setProjectId(testCase.getProject().getId());
                    defectDto.setModuleId(testCase.getModules().getId());
                    defectDto.setSubModuleId(testCase.getSubModule().getId());
                    defectDto.setSeverityId(testCase.getSeverity().getId());
                    defectDto.setPriorityId(releaseTestCaseDto.getPriorityId());
                    defectDto.setDefectStatusId(releaseTestCaseDto.getDefectStatusId());
                    defectDto.setDescription(testCase.getDescription());
                    StandardResponse createdDefect = defectService.createDefect(defectDto);
                    if (createdDefect.getStatusCode() == 2001) {
                        logger.info("Defect created successfully with ID: {}", defectDto.getDefectId());
                        return true;
                    } else {
                        logger.error("Failed to create defect for release test case ID: {}", id);
                        return false;
                    }
                } else {
                    logger.error("TestCase with ID {} not found for defect creation", testCaseId);
                    return false;
                }
            }
        }
        logger.info("Testcase passed");
        return true;
    }

    @Override
    public boolean releaseTestCaseExists(Long id) {
        Optional<ReleaseTestCase> releaseTestCase = releaseTestCaseRepository.findById(id);
        if (releaseTestCase.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ReleaseTestCaseDto getById(Long id){
        Optional<ReleaseTestCase> releaseTestCase = releaseTestCaseRepository.findById(id);
        if (releaseTestCase.isPresent()) {
            ReleaseTestCaseDto releaseTestCaseDto = new ReleaseTestCaseDto();
            BeanUtils.copyProperties(releaseTestCase, releaseTestCaseDto);
            return releaseTestCaseDto;
        } else {
            return null;
        }
    }

    @Override
    public ReleaseTestCaseDto updateReleaseTestCase(ReleaseTestCaseDto releaseTestCaseDto, int Status) {
        ReleaseTestCase releaseTestCase = new ReleaseTestCase();
        logger.info("Updating Release Test Case with ID: {}", releaseTestCaseDto);
        BeanUtils.copyProperties(releaseTestCaseDto, releaseTestCase);
        releaseTestCase.setTestCaseStatus(Status);
        logger.info("Updating Release Test Case with ID: {}", releaseTestCaseDto);
        ReleaseTestCase savedReleaseTestCase = releaseTestCaseRepository.save(releaseTestCase);
        BeanUtils.copyProperties(savedReleaseTestCase, releaseTestCaseDto);
        return releaseTestCaseDto;
    }

}
