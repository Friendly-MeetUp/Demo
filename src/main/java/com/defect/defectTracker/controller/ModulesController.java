package com.defect.defectTracker.controller;

import com.defect.defectTracker.dto.ModulesDto;
import com.defect.defectTracker.entity.Modules;
import com.defect.defectTracker.service.ModuleService;
import com.defect.defectTracker.utils.StandardResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/modules")
public class ModulesController {

    Logger logger = LoggerFactory.getLogger(ModulesController.class);

    @Autowired
    private ModuleService modulesService;

    @PostMapping
    public ResponseEntity<StandardResponse> createModule(@RequestBody ModulesDto modulesDto) {
        try {
            logger.info("Creating new module with DTO: {}", modulesDto);

            String moduleName = modulesDto.getModuleName();
            if (moduleName == null || moduleName.trim().isEmpty()) {
                return new ResponseEntity<>(new StandardResponse("failure", "Module name is required.", null, 4000), HttpStatus.BAD_REQUEST);
            }

            if (modulesDto.getProjectId() == null) {
                return new ResponseEntity<>(new StandardResponse("failure", "Project ID is required.", null, 4000), HttpStatus.BAD_REQUEST);
            }

            // Duplicate check first
            boolean exists = modulesService.checkIfModuleExists(moduleName.trim(), modulesDto.getProjectId());
            if (exists) {
                return new ResponseEntity<>(new StandardResponse("failure", "Module name already exists in this project.", null, 4090), HttpStatus.CONFLICT);
            }

            // Format checks
            if (moduleName.length() < 3 || moduleName.length() > 50) {
                return new ResponseEntity<>(new StandardResponse("failure", "Module name must be between 3 and 50 characters.", null, 4000), HttpStatus.BAD_REQUEST);
            }

            if (moduleName.matches("^[^a-zA-Z0-9]*$")) {
                return new ResponseEntity<>(new StandardResponse("failure", "Module name cannot contain only special characters.", null, 4000), HttpStatus.BAD_REQUEST);
            }
            if (moduleName.matches("^\\d+$")) {
                return new ResponseEntity<>(new StandardResponse(
                        "failure", "Module name cannot be only numbers.", null, 4000),
                        HttpStatus.BAD_REQUEST
                );
            }




            Modules createdModule = modulesService.createModule(modulesDto);
            if (createdModule == null) {
                return new ResponseEntity<>(new StandardResponse("failure", "Project not found.", null, 4000), HttpStatus.BAD_REQUEST);
            }

            logger.info("Module created successfully: {}", createdModule);
            return new ResponseEntity<>(new StandardResponse("success", "Saved successfully.", null, 2000), HttpStatus.CREATED);

        } catch (DataIntegrityViolationException e) {
            logger.error("Constraint violation while saving module", e);
            return new ResponseEntity<>(new StandardResponse("failure", "Module already exists (constraint error).", null, 4000), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error while creating module", e);
            return new ResponseEntity<>(new StandardResponse("failure", "Save Failed: " + e.getMessage(), null, 5000), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updateModule(@PathVariable Long id, @RequestBody ModulesDto modulesDto) {
        try {
            logger.info("Updating module with ID {} and DTO: {}", id, modulesDto);

            String moduleName = modulesDto.getModuleName();
            if (moduleName == null || moduleName.trim().isEmpty()) {
                return new ResponseEntity<>(new StandardResponse("failure", "Module name is required.", null, 4000), HttpStatus.BAD_REQUEST);
            }

            if (modulesDto.getProjectId() == null) {
                return new ResponseEntity<>(new StandardResponse("failure", "Project ID is required.", null, 4000), HttpStatus.BAD_REQUEST);
            }

            // Duplicate check first
            boolean duplicate = modulesService.isDuplicateModuleNameForUpdate(id, moduleName.trim(), modulesDto.getProjectId());
            if (duplicate) {
                return new ResponseEntity<>(new StandardResponse("failure", "Module name already exists in this project.", null, 4090), HttpStatus.CONFLICT);
            }

            // Format checks
            if (moduleName.length() < 3 || moduleName.length() > 50) {
                return new ResponseEntity<>(new StandardResponse("failure", "Module name must be between 3 and 50 characters.", null, 4000), HttpStatus.BAD_REQUEST);
            }

            if (moduleName.matches("^[^a-zA-Z0-9]*$")) {
                return new ResponseEntity<>(new StandardResponse("failure", "Module name cannot contain only special characters.", null, 4000), HttpStatus.BAD_REQUEST);
            }
            if (moduleName.matches("^\\d+$")) {
                return new ResponseEntity<>(new StandardResponse(
                        "failure", "Module name cannot be only numbers.", null, 4000),
                        HttpStatus.BAD_REQUEST
                );
            }


            modulesDto.setId(id);
            Modules updatedModule = modulesService.updateModule(modulesDto);
            if (updatedModule == null) {
                return new ResponseEntity<>(new StandardResponse("failure", "Module not found or update failed.", null, 4000), HttpStatus.NOT_FOUND);
            }

            logger.info("Module updated successfully: {}", updatedModule);
            return new ResponseEntity<>(new StandardResponse("success", "Updated successfully.", null, 2000), HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Unexpected error while updating module", e);
            return new ResponseEntity<>(new StandardResponse("failure", "Update Failed: " + e.getMessage(), null, 5000), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deleteModule(@PathVariable Long id) {
        try {
            logger.info("Attempting to delete module with ID: {}", id);

            // First check: Does the module even exist?
            boolean moduleExists = modulesService.checkIfModuleExistsById(id);
            if (!moduleExists) {
                logger.warn("Module with ID {} not found.", id);
                return new ResponseEntity<>(
                        new StandardResponse("failure", "Module not found.", null, 4040),
                        HttpStatus.NOT_FOUND
                );
            }

            // Proceed to try deletion
            boolean deleted = modulesService.deleteModuleById(id);

            if (deleted) {
                logger.info("Module with ID {} deleted successfully.", id);
                return new ResponseEntity<>(
                        new StandardResponse("success", "Deleted successfully.", null, 2000),
                        HttpStatus.OK
                );
            } else {
                logger.warn("Module with ID {} has related sub-modules.", id);
                return new ResponseEntity<>(
                        new StandardResponse("failure", "Cannot delete module: related sub-modules exist.", null, 4000),
                        HttpStatus.BAD_REQUEST
                );
            }

        } catch (Exception e) {
            logger.error("Unexpected error while deleting module with ID " + id, e);
            return new ResponseEntity<>(
                    new StandardResponse("failure", "Delete Failed: " + e.getMessage(), null, 5000),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @GetMapping("/project/{projectId}")
    public ResponseEntity<StandardResponse> getAllModulesByProjectId(@PathVariable Long projectId) {
        List<ModulesDto> modules = modulesService.getAllModulesByProjectId(projectId); //get project id
        if (modules == null || modules.isEmpty()) {
            return ResponseEntity.badRequest().body(new StandardResponse("failure", "Module not found.", null, 4000));
        }
        return ResponseEntity.ok(new StandardResponse("success", "Retrieved Successfully.", modules, 2000));
    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<StandardResponse> getModuleById(@PathVariable("moduleId") Long moduleId) {
        ModulesDto module = modulesService.getModuleById(moduleId); //get module id
        if (module == null) {
            return ResponseEntity.badRequest().body(new StandardResponse("failure", "Module not found.", null, 4000));
        }
        return ResponseEntity.ok(new StandardResponse("success", "Retrieved Successfully.", module, 2000));
    }

}
