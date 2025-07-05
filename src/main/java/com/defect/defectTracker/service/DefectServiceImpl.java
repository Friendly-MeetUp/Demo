package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.DefectDto;
import com.defect.defectTracker.entity.*;
import com.defect.defectTracker.repository.*;
import com.defect.defectTracker.utils.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
//import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.sql.Time;
import java.util.*;
import java.util.Date;

import java.util.List;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.util.stream.Collectors;

import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import java.io.PrintWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStreamReader;
import java.io.Reader;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional

public class DefectServiceImpl implements DefectService {

    Logger logger = LoggerFactory.getLogger(DefectServiceImpl.class);

    @Autowired private DefectRepository defectRepository;
    @Autowired private SeverityRepository severityRepository;
    @Autowired private PriorityRepository priorityRepository;
    @Autowired  private DefectStatusRepository defectStatusRepository;
    @Autowired private ReleaseTestCaseRepository releaseTestCaseRepository;
    @Autowired private DefectTypeRepository defectTypeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private DefectHistoryRepository defectHistoryRepository;
    private String lastDefectId;

    //01----------------------------------------------------------------------------------------------------------------

    private String generateDefectId(String lastId) {
        int nextId = 1;
        if (lastId != null && lastId.startsWith("DF")) {
            try {
                nextId = Integer.parseInt(lastId.substring(2)) + 1;
            } catch (NumberFormatException ignored) {}
        }

        String newId;
        do {
            newId = String.format("DF%05d", nextId++);
        } while (defectRepository.existsByDefectId(newId));

        return newId;
    }

    @Transactional
    @Override
    public StandardResponse createDefect(DefectDto dto) {
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            return new StandardResponse("Failure", "Description is mandatory", null, 2000);
        }
        if (dto.getSeverityId() == null) {
            return new StandardResponse("Failure", "Severity ID is mandatory", null, 4000);
        }
        if (dto.getPriorityId() == null) {
            return new StandardResponse("Failure", "Priority ID is mandatory", null, 4000);
        }
        if (dto.getDefectStatusId() == null) {
            return new StandardResponse("Failure", "Defect Status ID is mandatory", null, 4000);
        }

        try {
            Defect defect = new Defect();
            defect.setDefectId(generateDefectId(lastDefectId));
            logger.info("Creating Defect with defect id {}", defect.getDefectId());
            defect.setDescription(dto.getDescription());
            logger.info(dto.getDescription());

            defect.setSeverity(severityRepository.findById(dto.getSeverityId())
                    .orElseThrow(() -> new RuntimeException("Severity ID " + dto.getSeverityId() + " not found")));

            defect.setPriority(priorityRepository.findById(dto.getPriorityId())
                    .orElseThrow(() -> new RuntimeException("Priority ID " + dto.getPriorityId() + " not found")));

            defect.setDefectStatus(defectStatusRepository.findById(dto.getDefectStatusId())
                    .orElseThrow(() -> new RuntimeException("DefectStatus ID " + dto.getDefectStatusId() + " not found")));

            defect.setAttachment(dto.getAttachment());
            defect.setSteps(dto.getSteps());

            defect.setReleaseTestCase(releaseTestCaseRepository.findById(dto.getReleaseTestCaseId())
                    .orElseThrow(() -> new RuntimeException("ReleaseTestCase ID " + dto.getReleaseTestCaseId() + " not found")));

            logger.info(String.valueOf(dto.getReleaseTestCaseId()));

            defect.setDefectType(defectTypeRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new RuntimeException("DefectType ID " + dto.getTypeId() + " not found")));

            defect.setAssignedBy(userRepository.findById(dto.getAssignbyId())
                    .orElseThrow(() -> new RuntimeException("AssignBy User ID " + dto.getAssignbyId() + " not found")));

            defect.setAssignedTo(userRepository.findById(dto.getAssigntoId())
                    .orElseThrow(() -> new RuntimeException("AssignTo User ID " + dto.getAssigntoId() + " not found")));

            defect.setProject(projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project ID " + dto.getProjectId() + " not found")));

            defect.setModules(defect.getReleaseTestCase().getTestCase().getModules());
            defect.setSubModule(defect.getReleaseTestCase().getTestCase().getSubModule());

            defectRepository.save(defect);
//            DefectHistory Created
            saveDefectHistory(defect, "Created");

            return new StandardResponse("Success", "Saved successfully", null, 2000);
        } catch (Exception e) {
            logger.error("Error creating defect", e);
            return new StandardResponse("Failure", e.getMessage(), null, 4000);
        }
    }
    @Transactional
    @Override
    public StandardResponse updateDefect(Long id, DefectDto dto) {
        try {
            Defect defect = defectRepository.findById(id).orElse(null);
            if (defect == null) {
                return new StandardResponse("Failure", "Defect with id " + id + " not found", null, 4000);
            }

            // Validate mandatory fields
            if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
                return new StandardResponse("Failure", "Description is mandatory", null, 4000);
            }
            if (dto.getSeverityId() == null || dto.getPriorityId() == null || dto.getDefectStatusId() == null) {
                return new StandardResponse("Failure", "Severity, Priority, and Status are mandatory", null, 4000);
            }
//
            if (dto.getProjectId() == null) {
                return new StandardResponse("Failure", "Project ID is mandatory", null, 4000);
            }


            // Update fields (defectId not changed)
            defect.setDescription(dto.getDescription());
            defect.setSeverity(severityRepository.findById(dto.getSeverityId()).orElseThrow());
            defect.setPriority(priorityRepository.findById(dto.getPriorityId()).orElseThrow());
            defect.setDefectStatus(defectStatusRepository.findById(dto.getDefectStatusId()).orElseThrow());
            defect.setAttachment(dto.getAttachment());
            defect.setSteps(dto.getSteps());
            defect.setDefectType(defectTypeRepository.findById(dto.getTypeId()).orElseThrow());
            defect.setAssignedBy(userRepository.findById(dto.getAssignbyId()).orElseThrow());
            defect.setAssignedTo(userRepository.findById(dto.getAssigntoId()).orElseThrow());
            defect.setProject(projectRepository.findById(dto.getProjectId()).orElseThrow());

            // Validate and update ReleaseTestCase only if it’s different
            if (dto.getReleaseTestCaseId() != null &&
                    (defect.getReleaseTestCase() == null ||
                            !dto.getReleaseTestCaseId().equals(defect.getReleaseTestCase().getId()))) {

                // Make sure the new releaseTestCase is not assigned to another defect (if uniqueness enforced)
                ReleaseTestCase releaseTestCase = releaseTestCaseRepository.findById(dto.getReleaseTestCaseId())
                        .orElseThrow();

                defect.setReleaseTestCase(releaseTestCase);

                // Auto-update modules and submodule from testCase
                defect.setModules(releaseTestCase.getTestCase().getModules());
                defect.setSubModule(releaseTestCase.getTestCase().getSubModule());
            }

            defectRepository.save(defect);
//            DefectHistory Updated
            saveDefectHistory(defect, "Updated");

            return new StandardResponse("Success", "Updated successfully", null, 2000);
        } catch (Exception e) {
            logger.error("Error updating defect with ID {}: {}", id, e.getMessage(), e);
            return new StandardResponse("Failure", "Update Failed", null, 4000);
        }
    }

    @Override
    public ResponseEntity<StandardResponse> filterDefects(DefectDto filterDto) {
        try {
            if (filterDto.getProjectId() == null) {
                return ResponseEntity.ok().body(
                        new StandardResponse("failure", "Project ID is mandatory", null,
                                4000)
                );
            }

            List<Defect> defects = defectRepository.filterDefects(
                    filterDto.getProjectId(),
                    filterDto.getDefectStatusId(),
                    filterDto.getSeverityId(),
                    filterDto.getPriorityId(),
                    filterDto.getTypeId(),
                    filterDto.getReleaseTestCaseId(),
                    filterDto.getAssignbyId(),
                    filterDto.getAssigntoId(),
                    filterDto.getModuleId(),
                    filterDto.getSubModuleId()
            );

            if (defects.isEmpty()) {
                return ResponseEntity.ok().body(
                        new StandardResponse("failure", "No defects found with the given criteria", null,
                                HttpStatus.OK.value())
                );
            }

            List<Map<String, Object>> mappedList = defects.stream()
                    .map(this::mapDefectToCustomResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(
                    new StandardResponse("success", "Retrieved Successfully", mappedList,
                            HttpStatus.OK.value())
            );
        } catch (Exception e) {
            return ResponseEntity.ok().body(
                    new StandardResponse("error", "Retrieve Failed: " + e.getMessage(), null, 4000)
            );
        }
    }
    private Map<String, Object> mapDefectToCustomResponse(Defect defect) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("defectId", defect.getDefectId());
        map.put("description", defect.getDescription());
        map.put("reOpenCount", defect.getReOpenCount());
        map.put("attachment", defect.getAttachment());
        map.put("steps", defect.getSteps());

        // Custom mappings
        map.put("project_name", defect.getProject() != null ? defect.getProject().getProjectName() : null);
        map.put("severity_name", defect.getSeverity() != null ? defect.getSeverity().getSeverityName() : null);

        if (defect.getPriority() != null) {
            map.put("priority_name", defect.getPriority().getPriority());
            map.put("priority", defect.getPriority().getPriority());
        } else {
            map.put("priority_name", null);
            map.put("priority", null);
        }

        map.put("defect_status_name", defect.getDefectStatus() != null ? defect.getDefectStatus().getDefectStatusName() : null);

        map.put("release_test_case_description",
                defect.getReleaseTestCase() != null && defect.getReleaseTestCase().getTestCase() != null
                        ? defect.getReleaseTestCase().getTestCase().getDescription() : null);

        map.put("assigned_by_name", defect.getAssignedBy() != null ? defect.getAssignedBy().getFirstName() : null);
        map.put("assigned_to_name", defect.getAssignedTo() != null ? defect.getAssignedTo().getFirstName() : null);

        map.put("defect_type_name", defect.getDefectType() != null ? defect.getDefectType().getDefectTypeName() : null);
        map.put("module_name", defect.getModules() != null ? defect.getModules().getModuleName() : null);
        map.put("sub_module_name", defect.getSubModule() != null ? defect.getSubModule().getSubModuleName() : null);

        return map;
    }

    //05----------------------------------------------------------------------------------------------------------------
    @Transactional
    @Override
    public int importDefectsFromCsv(MultipartFile file) throws IOException {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {

            for (CSVRecord record : parser) {
                try {
                    Defect defect = new Defect();

                    // Generate new unique defect ID
                    String lastDefectId = defectRepository.findTopDefectId();
                    String newId = generateDefectId(lastDefectId);
                    defect.setDefectId(newId);

                    // Mandatory fields
                    defect.setDescription(record.get("description"));
                    defect.setReOpenCount(Integer.parseInt(record.get("re_open_count")));
                    defect.setAttachment(record.get("attachment"));
                    defect.setSteps(record.get("steps"));

                    // Lookup and set related entities using names
                    projectRepository.findByProjectName(record.get("project_name")).ifPresent(defect::setProject);
                    severityRepository.findBySeverityName(record.get("severity_name")).ifPresent(defect::setSeverity);
                    priorityRepository.findByPriority(record.get("priority_name")).ifPresent(defect::setPriority);
                    defectStatusRepository.findByDefectStatusName(record.get("defect_status_name")).ifPresent(defect::setDefectStatus);
                    defectTypeRepository.findByDefectTypeName(record.get("defect_type_name")).ifPresent(defect::setDefectType);

                    userRepository.findByFirstName(record.get("assigned_by_name")).ifPresent(defect::setAssignedBy);
                    userRepository.findByFirstName(record.get("assigned_to_name")).ifPresent(defect::setAssignedTo);

                    // Fetch ReleaseTestCase and its associated Module/SubModule
                    List<ReleaseTestCase> releaseTestCases = releaseTestCaseRepository.findByTestCase_Description(record.get("release_test_case_description"));
                    if (!releaseTestCases.isEmpty()) {
                        ReleaseTestCase releaseTestCase = releaseTestCases.get(0);

                        // Avoid duplicate releaseTestCase
                        if (defectRepository.existsByReleaseTestCaseId(releaseTestCase.getId())) {
                            log.warn("Duplicate release_test_case_id [{}] found. Skipping row {}.", releaseTestCase.getId(), record.getRecordNumber());
                            continue;
                        }

                        defect.setReleaseTestCase(releaseTestCase);

                        if (releaseTestCase.getTestCase() != null) {
                            defect.setModules(releaseTestCase.getTestCase().getModules());
                            defect.setSubModule(releaseTestCase.getTestCase().getSubModule());
                        }
                    } else {
                        log.warn("No ReleaseTestCase found for: {}", record.get("release_test_case_description"));
                    }

                    // Save defect
                    defectRepository.save(defect);

                    saveDefectHistory(defect, "Created");

                    count++;
                } catch (Exception ex) {
                    log.error("Failed to import record: {}", record.toString(), ex);
                }
            }
        }

        return count;
    }

    @Override
    public void exportDefects(HttpServletResponse response) throws IOException {
        List<Defect> defects = defectRepository.findAll();

        // Set response headers
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"defects.csv\"");

        try (
                PrintWriter writer = response.getWriter();
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                        "defect_id", "description", "re_open_count", "attachment", "steps",
                        "project_name", "severity_name", "priority_name", "priority", "defect_status_name",
                        "defect_type_name",
                        "release_test_case_description", "assigned_by_name", "assigned_to_name",
                        "module_name", "sub_module_name"))
        ) {
            for (Defect d : defects) {
                csvPrinter.printRecord(
                        d.getDefectId(),
                        d.getDescription(),
                        d.getReOpenCount(),
                        d.getAttachment(),
                        d.getSteps(),
                        d.getProject() != null ? d.getProject().getProjectName() : "",
                        d.getSeverity() != null ? d.getSeverity().getSeverityName() : "",
                        d.getPriority() != null ? d.getPriority().getPriority() : "",
                        d.getPriority() != null ? d.getPriority().getPriority() : "", // Duplicate kept if needed
                        d.getDefectStatus() != null ? d.getDefectStatus().getDefectStatusName() : "",
                        d.getDefectType() != null ? d.getDefectType().getDefectTypeName() : "",
                        d.getReleaseTestCase() != null && d.getReleaseTestCase().getTestCase() != null
                                ? d.getReleaseTestCase().getTestCase().getDescription() : "",
                        d.getAssignedBy() != null ? d.getAssignedBy().getFirstName() : "",
                        d.getAssignedTo() != null ? d.getAssignedTo().getFirstName() : "",
                        d.getModules() != null ? d.getModules().getModuleName() : "",
                        d.getSubModule() != null ? d.getSubModule().getSubModuleName() : ""
                );
            }

            csvPrinter.flush();
        } catch (IOException e) {
            logger.error("Failed to export defects to CSV", e);
            throw new RuntimeException("CSV export failed", e);
        }
    }

//--------------------------------------------------------------------------



    private void saveDefectHistory (Defect defect, String actionStatus){
        DefectHistory history = new DefectHistory();
        history.setDefectDate(new Date());
        history.setDefectTime(new Time(System.currentTimeMillis()));
        history.setPreviousStatus(actionStatus);
        history.setAssignedTo(defect.getAssignedTo().getUserId());
        history.setAssignedBy(defect.getAssignedBy().getUserId());
        history.setReleaseId(defect.getReleaseTestCase().getReleases().getId());
        history.setDefectStatus(defect.getDefectStatus().getDefectStatusName());
        history.setDefect(defect);

        defectHistoryRepository.save(history);
    }

    @Override
    public Map<String, Object> getDefectCustomResponseById(Long id) {
        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No defect found for this id"));

        return mapDefectToCustomResponse(defect);
    }


    //10---------------------------------------------------------------------------------------------------------------
    @Override
    public List<Map<String, Object>> getDefectsCustomResponseByProjectId(Long projectId) {
        List<Defect> defects = defectRepository.findByProjectId(projectId);

        return defects.stream()
                .map(this::mapDefectToCustomResponse)
                .collect(Collectors.toList());
    }


    //11----------------------------------------------------------------------------------------------------------------
    @Override
    public Map<String, Object> getDefectCustomResponseByReleaseTestCaseId(Long releaseTestCaseId) {
        Defect defect = defectRepository.findByReleaseTestCaseId(releaseTestCaseId);
        if (defect == null) {
            throw new NoSuchElementException("No defect found for this releaseTestCaseId");
        }

        return mapDefectToCustomResponse(defect);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Override
    public List<Map<String, Object>> fetchDefectsByTestCaseIdWithDetails(Long testCaseId) {
        List<Defect> defects = defectRepository.findByReleaseTestCaseTestCaseId(testCaseId);
        if (defects.isEmpty()) {
            throw new NoSuchElementException("No defects found for this testCaseId");
        }
        return defects.stream()
                .map(this::mapDefectToCustomResponse)
                .collect(Collectors.toList());
    }
//Delete ServiceImpl
@Transactional
@Override
public StandardResponse deleteDefectById(Long id) {
    log.info("Attempting to delete defect with ID: {}", id);

    // Step 1: Check if defect exists
    Defect defect = defectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Defect not found with id: " + id));

//    DefectHistory store as "Deleted"
    saveDefectHistory(defect, "Deleted");


    // Step 2: Delete related defect_history records by defect ID
    defectRepository.deleteByDefectId(id);
    log.info("Deleted related defect_history records for defect ID: {}", id);

    // Step 3: Detach associations to avoid FK constraint issues
    defect.setAssignedBy(null);
    defect.setAssignedTo(null);
    defect.setProject(null);
    defect.setDefectStatus(null);
    defect.setDefectType(null);
    defect.setPriority(null);
    defect.setSeverity(null);
    defect.setReleaseTestCase(null);
    defect.setModules(null);
    defect.setSubModule(null);

    // Step 4: Delete defect
    defectRepository.delete(defect);
    log.info("Successfully deleted defect with ID: {}", id);

    return new StandardResponse(
            "Success",
            "Defect deleted successfully",
            null,
            2000
    );
}

    @Override
    public List<DefectDto> getDefectsByProjectId(Long projectId) {
        return List.of();
    }

    @Override
    public DefectDto getDefectsByReleaseTestCaseId(Long releaseTestCaseId) {
        return null;
    }

}

