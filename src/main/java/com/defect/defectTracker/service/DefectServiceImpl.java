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

import java.sql.Time;
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

    //01----------------------------------------------------------------------------------------------------------------
    private String generateDefectId() {
        String lastId = defectRepository.findTopByOrderByIdDesc();
        int number = 1;
        if (lastId != null && lastId.startsWith("DF")) {
            number = Integer.parseInt(lastId.substring(2)) + 1;
        }
        return String.format("DF%05d", number);
    }
//    private String generateDefectId() {
//        String lastId = String.valueOf(defectRepository.findTopByOrderByIdDesc());
//        logger.info("Db" + lastId);// e.g., "DF00023"
//        int number = 1;
//
//        if (lastId != null && lastId.startsWith("DF")) {
//            try {
//                number = Integer.parseInt(lastId.substring(2)) + 1;
//            } catch (NumberFormatException e) {
//                logger.warn("Invalid defect ID format: {}", lastId);
//            }
//        }
//
//        return String.format("DF%05d", number); // e.g., DF00024
//
//    }



    //02---NOAPI DOCument-------------------------------------------------------------------------------------------------------
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Override
//    public DefectDto getByReleaseTestCaseId(Long testCaseId) {
//        log.info("Fetching defect with ID: {}", testCaseId);
//        DefectDto defectDto = new DefectDto();
//        Optional<Defect> defect = defectRepository.findById(testCaseId);
//        this.modelMapper.map(defect, defectDto);
//        return defectDto;
    //  }

    //03---NOAPI DOCument-------------------------------------------------------------------------------------------------------------
    //no working
//    @Override
//    public DefectDto getById(Long defectId) {
//        log.info("Fetching defect with ID: {}", defectId);
//        DefectDto defectDto = new DefectDto();
//        Optional<Defect> defect = defectRepository.findById(defectId);
//        this.modelMapper.map(defect, defectDto);
//        return defectDto;
//    }
    //04----------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<StandardResponse> filterDefects(DefectDto filterDto) {
        try {
            if (filterDto.getProjectId() == null) {
                return ResponseEntity.badRequest().body(
                        new StandardResponse("failure", "Project ID is mandatory", null,
                                HttpStatus.BAD_REQUEST.value())
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
                    filterDto.getAssigntoId()
            );

            if (defects.isEmpty()) {
                return ResponseEntity.ok().body(
                        new StandardResponse("failure", "No defects found with the given criteria", null,
                                HttpStatus.OK.value()
                        )
                );
            }

            List<DefectDto> dtoList = defects.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(
                    new StandardResponse("success", "Retrieved Successfully", dtoList,
                            HttpStatus.OK.value()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Retrieve Failed: " + e.getMessage(), null,
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }
    }

    private DefectDto convertToDto(Defect defect) {
        DefectDto dto = new DefectDto();
        dto.setDefectId(defect.getDefectId());
        dto.setDescription(defect.getDescription());
        dto.setAttachment(defect.getAttachment());
        dto.setSteps(defect.getSteps());
        dto.setReOpenCount(defect.getReOpenCount());

        if (defect.getReleaseTestCase() != null)
            dto.setReleaseTestCaseId(defect.getReleaseTestCase().getId());

        if (defect.getAssignedBy() != null)
            dto.setAssignbyId(defect.getAssignedBy().getId());

        if (defect.getAssignedTo() != null)
            dto.setAssigntoId(defect.getAssignedTo().getId());

        if (defect.getProject() != null)
            dto.setProjectId(defect.getProject().getId());

        if (defect.getSeverity() != null)
            dto.setSeverityId(defect.getSeverity().getId());

        if (defect.getPriority() != null)
            dto.setPriorityId(defect.getPriority().getId());

        if (defect.getDefectStatus() != null)
            dto.setDefectStatusId(defect.getDefectStatus().getId());

        if (defect.getDefectType() != null)
            dto.setTypeId(defect.getDefectType().getId());

        if (defect.getModules() != null)
            dto.setModuleId(defect.getModules().getId());

        if (defect.getSubModule() != null)
            dto.setSubModuleId(defect.getSubModule().getId());

        return dto;
    }
    //05----------------------------------------------------------------------------------------------------------------
    @Override
    public void exportDefects(HttpServletResponse response) throws IOException {
        List<Defect> defects = defectRepository.findAll();

        // Set response headers
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"defects.csv\"");

        try (PrintWriter writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("defect_id", "description", "re_open_count", "attachment", "steps",
                             "release_test_case_name", "assigned_by_name", "assigned_to_name",
                             "severity_name", "defect_status_name", "project_name", "priority_name",
                             "type_name", "modules_name", "sub_module_name"))) {

            for (Defect d : defects) {

                logger.info("Defect id is " + d.getDefectId());
                csvPrinter.printRecord(
                        d.getDefectId(),
                        d.getDescription(),
                        d.getReOpenCount(),
                        d.getAttachment(),
                        d.getSteps(),
                        d.getReleaseTestCase() != null && d.getReleaseTestCase().getTestCase() != null
                                ? d.getReleaseTestCase().getTestCase().getDescription() : "",

                        d.getAssignedBy() != null ? d.getAssignedBy().getFirstName() : "",
                        d.getAssignedTo() != null ? d.getAssignedTo().getFirstName() : "",
                        d.getSeverity() != null ? d.getSeverity().getSeverityName() : "",
                        d.getDefectStatus() != null ? d.getDefectStatus().getDefectStatusName() : "",
                        d.getProject() != null ? d.getProject().getProjectName() : "",
                        d.getPriority() != null ? d.getPriority().getPriority() : "",
                        d.getDefectType() != null ? d.getDefectType().getDefectTypeName() : "",
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
    //06----------------------------------------------------------------------------------------------------------------
//    @Transactional
//    @Override
//    public StandardResponse createDefect(DefectDto dto) {
//        log.info("Creating Defect with DTO: {}", dto);
//        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
//            return new StandardResponse("Failure", "Description is mandatory", null, 4000);
//        }
//        if (dto.getSeverityId() == null) {
//            return new StandardResponse("Failure", "Severity ID is mandatory", null, 4000);
//        }
//        if (dto.getPriorityId() == null) {
//            return new StandardResponse("Failure", "Priority ID is mandatory", null, 4000);
//        }
//        if (dto.getDefectStatusId() == null) {
//            return new StandardResponse("Failure", "Defect Status ID is mandatory", null, 4000);
//        }
//
//
//        try {
//            Defect defect = new Defect();
//            defect.setDefectId(generateDefectId());
//            log.info("Creating Defect with defect id {}", defect.getDefectId());
//            defect.setDescription(dto.getDescription());
//            log.info(dto.getDescription());
//            defect.setSeverity(severityRepository.findById(dto.getSeverityId()).orElseThrow());
//            log.info("Setted Severity in create defect: {}", defect.getSeverity());
//            defect.setPriority(priorityRepository.findById(dto.getPriorityId()).orElseThrow());
//            log.info("Setted Priority in create defect: {}", defect.getPriority());
//            log.info("Defect Status ID: {}", dto.getDefectStatusId());
//            defect.setDefectStatus(defectStatusRepository.findById(dto.getDefectStatusId()).orElseThrow());
//            log.info("Setted Defect Status in create defect: {}", defect.getDefectStatus());
//            defect.setAttachment(dto.getAttachment());
//            defect.setSteps(dto.getSteps());
//            defect.setReleaseTestCase(releaseTestCaseRepository.findById(dto.getReleaseTestCaseId()).orElseThrow());
//            log.info("Get Release test Case : {}", (dto.getReleaseTestCaseId()));
//            defect.setDefectType(defectTypeRepository.findById(dto.getTypeId()).orElseThrow());
//            log.info("Setted Defect Type in create defect: {}", defect.getDefectType());
//            defect.setProject(projectRepository.findById(dto.getProjectId()).orElseThrow());
//            log.info("Setted Defect Type in create defect: {}", defect.getProject());
//            defect.setModules(defect.getReleaseTestCase().getTestCase().getModules());
//            log.info("Setted Defect Type in create defect: {}", defect.getModules());
//            defect.setSubModule(defect.getReleaseTestCase().getTestCase().getSubModule());
//            log.info("Setted Defect Type in create defect: {}", defect.getSubModule());
//
//            defectRepository.save(defect);
////            saveDefectHistory(defect,"created");
//
//            if (defect != null) {
////                log.info("Defect created successfully with ID: {}", defect.getId());
//                return new StandardResponse("Success", "Saved successfully", null, 2001);
//            } else {
////                log.error("Defect creation failed");
//                return new StandardResponse("Failure", "Save Failed", null, 4000);
//            }
//        } catch (Exception e) {
    ////            throw new RuntimeException(e);
//            return new StandardResponse("Failure", "Save Failed", null, 4000);
//
//        }
//    }
    @Transactional
    @Override
    public StandardResponse createDefect(DefectDto dto) {
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            return new StandardResponse("Failure", "Description is mandatory", null, 4000);
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

//    try {
//        Defect defect = new Defect();
//        defect.setDefectId(generateDefectId());
//        logger.info("Creating Defect with defect id {}", defect.getDefectId());
//        defect.setDescription(dto.getDescription());
//        logger.info(dto.getDescription());
//        defect.setSeverity(severityRepository.findById(dto.getSeverityId()).orElseThrow());
//        defect.setPriority(priorityRepository.findById(dto.getPriorityId()).orElseThrow());
//        defect.setDefectStatus(defectStatusRepository.findById(dto.getDefectStatusId()).orElseThrow());
//        defect.setAttachment(dto.getAttachment());
//        defect.setSteps(dto.getSteps());
//        defect.setReleaseTestCase(releaseTestCaseRepository.findById(dto.getReleaseTestCaseId()).orElseThrow());
//        logger.info(String.valueOf(dto.getReleaseTestCaseId()));
//        defect.setDefectType(defectTypeRepository.findById(dto.getTypeId()).orElseThrow());
//        defect.setAssignedBy(userRepository.findById(dto.getAssignbyId()).orElseThrow());
//        defect.setAssignedTo(userRepository.findById(dto.getAssigntoId()).orElseThrow());
//        defect.setProject(projectRepository.findById(dto.getProjectId()).orElseThrow());
//        defect.setModules(defect.getReleaseTestCase().getTestCase().getModules());
//        defect.setSubModule(defect.getReleaseTestCase().getTestCase().getSubModule());
//
//        defectRepository.save(defect);
//        saveDefectHistory(defect, "Created");
//
//
//
//        return new StandardResponse("Success", "Saved successfully", null, 2000);
//    } catch (Exception e) {
//        logger.error("Error", e);
//        return new StandardResponse("Failure", "Something went wrong!", null, 4000);       }
        try {
            Defect defect = new Defect();
            defect.setDefectId(generateDefectId());
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
            saveDefectHistory(defect, "Updated");

            return new StandardResponse("Success", "Updated successfully", null, 2000);
        } catch (Exception e) {
            logger.error("Error updating defect with ID {}: {}", id, e.getMessage(), e);
            return new StandardResponse("Failure", "Update Failed", null, 4000);
        }
    }

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

//        defectHistoryRepository.save(history);
    }
    @Transactional
    @Override
    public StandardResponse deleteDefectById (Long id){
        log.info("Attempting to delete defect with ID: {}", id);

        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Defect not found with id: " + id));

        // Detach references to avoid foreign key/cascade issues
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
    public DefectDto getDefectById (Long id){
        Defect defect = defectRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("No defect found for this id"));

        return mapToDto(defect);
    }
    //10---------------------------------------------------------------------------------------------------------------
    @Override
    public List<DefectDto> getDefectsByProjectId (Long projectId){
        List<Defect> defects = defectRepository.findByProjectId(projectId);

        if (defects.isEmpty()) {
            throw new NoSuchElementException("No defect found for this projectId");
        }

        return defects.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    private DefectDto mapToDto (Defect defect){
        return DefectDto.builder()
                .defectId(defect.getDefectId())
                .description(defect.getDescription()) // mapped to descriptions in DTO
                .reOpenCount(defect.getReOpenCount())
                .attachment(defect.getAttachment())
                .steps(defect.getSteps())
                .releaseTestCaseId(defect.getReleaseTestCase().getId())
                .assignbyId(defect.getAssignedBy().getId())
                .assigntoId(defect.getAssignedTo().getId())
                .severityId(defect.getSeverity().getId())
                .defectStatusId(defect.getDefectStatus().getId())
                .projectId(defect.getProject().getId())
                .priorityId(defect.getPriority().getId())
                .typeId(defect.getDefectType().getId())
                .moduleId(defect.getModules() != null ? defect.getModules().getId() : null)
                .subModuleId(defect.getSubModule() != null ? defect.getSubModule().getId() : null)
                .build();
    }

    //11----------------------------------------------------------------------------------------------------------------
    @Override
    public DefectDto getDefectsByReleaseTestCaseId (Long releaseTestCaseId){
        Defect defects = defectRepository.findByReleaseTestCaseId(releaseTestCaseId);

        DefectDto defectDto = mapToDto(defects);

        return defectDto;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public Integer uploadDefects (MultipartFile file) throws IOException {
        int importedCount = 0;

        int nextIdNumber = 1;
        String lastDefect = defectRepository.findTopByOrderByIdDesc(); // Ensure this method exists

        if (lastDefect != null && lastDefect.startsWith("DF")) {
            try {
                nextIdNumber = Integer.parseInt(lastDefect.substring(2)) + 1;
            } catch (NumberFormatException e) {
                logger.warn("Invalid defect_id format: {}", lastDefect);
            }
        }


        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {

            logger.info("Starting import of defects from CSV file");

            for (CSVRecord record : csvParser) {
                Defect defect = new Defect();
                defect.setDefectId(String.format("DF%05d", nextIdNumber++));

                defect.setDescription(record.get("description"));
                defect.setSteps(record.get("steps"));
                defect.setAttachment(record.isMapped("attachment") ? record.get("attachment") : null);

                if (record.isMapped("re_open_count") && !record.get("re_open_count").isEmpty()) {
                    try {
                        defect.setReOpenCount(Integer.parseInt(record.get("re_open_count")));
                    } catch (NumberFormatException e) {
                        logger.warn("Invalid re_open_count: {}", record.get("re_open_count"));
                        defect.setReOpenCount(0);
                    }
                }

                defect.setProject(projectRepository.findById(Long.valueOf(record.get("project_id"))).orElse(null));
                defect.setSeverity(severityRepository.findById(Long.valueOf(record.get("severity_id"))).orElse(null));
                defect.setDefectType(defectTypeRepository.findById(Long.valueOf(record.get("type_id"))).orElse(null));
                defect.setAssignedBy(userRepository.findById(Long.valueOf(record.get("assigned_by"))).orElse(null));
                defect.setAssignedTo(userRepository.findById(Long.valueOf(record.get("assigned_to"))).orElse(null));
                defect.setPriority(priorityRepository.findById(Long.valueOf(record.get("priority_id"))).orElse(null));
                defect.setDefectStatus(defectStatusRepository.findById(Long.valueOf(record.get("defect_status_id"))).orElse(null));
                defect.setReleaseTestCase(releaseTestCaseRepository.findById(Long.valueOf(record.get("release_test_case_id"))).orElse(null));
                //defect.setReleaseTestCase(releaseTestCaseRepository.findById(Long.valueOf(record.get("release_test_case_id"))).orElse(null));

                defect.setModules(defect.getReleaseTestCase().getTestCase().getModules());
                defect.setSubModule(defect.getReleaseTestCase().getTestCase().getSubModule());
                if (defect.getDefectStatus() == null) {
                    logger.warn("Skipping defect [{}]: missing or invalid defect_status_id", defect.getDefectId());
                    continue;
                }

                defectRepository.save(defect);
                importedCount++;
            }

            logger.info("Total defects imported: {}", importedCount);
        } catch (IOException e) {
            logger.error("Failed to import defects from CSV", e);
            throw new RuntimeException("CSV import failed", e);
        }

        return importedCount;
    }
    //--------------------------------------------------------------------------------------------
    @Override
    public void exportdefectIds (HttpServletResponse response) throws IOException {
        List<Defect> defects = defectRepository.findAll();

        try (PrintWriter writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("defect_id", "description", "re_open_count", "attachment", "steps",
                             "release_test_case_id", "assigned_by", "assigned_to",
                             "severity_id", "defect_status_id", "project_id", "priority_id", "type_id"
                     ))) {

            for (Defect d : defects) {
                csvPrinter.printRecord(
                        d.getDefectId(),
                        d.getDescription(),
                        d.getReOpenCount(),
                        d.getAttachment(),
                        d.getSteps(),
                        d.getReleaseTestCase() != null ? d.getReleaseTestCase().getId() : "",
                        d.getAssignedBy() != null ? d.getAssignedBy().getId() : "",
                        d.getAssignedTo() != null ? d.getAssignedTo().getId() : "",
                        d.getSeverity() != null ? d.getSeverity().getId() : "",
                        d.getDefectStatus() != null ? d.getDefectStatus().getId() : "",
                        d.getProject() != null ? d.getProject().getId() : "",
                        d.getPriority() != null ? d.getPriority().getId() : "",
                        d.getDefectType() != null ? d.getDefectType().getId() : ""
//                        d.getModules() != null ? d.getModules().getId() : "",
//                        d.getSubModule() != null ? d.getSubModule().getId() : ""
                );
            }

            csvPrinter.flush();
        } catch (IOException e) {
            logger.error("Failed to export defects to CSV", e);
            throw new RuntimeException("CSV export failed", e);
        }
    }

}