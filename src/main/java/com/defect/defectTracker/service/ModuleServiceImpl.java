package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ModulesDto;
import com.defect.defectTracker.entity.Modules;
import com.defect.defectTracker.entity.Project;
import com.defect.defectTracker.repository.ModulesRepository;
import com.defect.defectTracker.repository.ProjectRepository;
import com.defect.defectTracker.repository.SubModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.defect.defectTracker.repository.ModulesRepository;
import com.defect.defectTracker.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ModuleServiceImpl implements ModuleService {
    @Autowired
    private SubModuleRepository subModuleRepository;


    @Autowired
    private ModulesRepository modulesRepository;

    @Autowired
    private ProjectRepository projectRepository;


    Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

    @Override
    public boolean checkIfModuleExists(String moduleName, Long projectId) {
        return modulesRepository.existsByModuleNameAndProject_Id(moduleName, projectId);
    }



    @Override
    public Modules createModule(ModulesDto modulesDto) {
        Optional<Project> optionalProject = projectRepository.findById(modulesDto.getProjectId());

        if (optionalProject.isEmpty()) {
            return null;
        }

        logger.info("New Module Id is before : {}", modulesDto.getModuleId());

        String newModuleId = generateNextModuleId();

        logger.info("New Module Id is after : {}", newModuleId);

        Modules module = new Modules();
        module.setModuleId(newModuleId);
        module.setModuleName(modulesDto.getModuleName());
        module.setProject(optionalProject.get());

        return modulesRepository.save(module);
    }

    private String generateNextModuleId() {
        Optional<Modules> lastModuleOpt = modulesRepository.findTopByOrderByModuleIdDesc();

        int nextNum = 1;
        if (lastModuleOpt.isPresent()) {
            String lastModuleId = lastModuleOpt.get().getModuleId();
            if (lastModuleId != null && lastModuleId.startsWith("MO")) {
                String numberPart = lastModuleId.substring(2);
                try {
                    nextNum = Integer.parseInt(numberPart) + 1;
                } catch (NumberFormatException e) {
                    logger.info("Failed to parse module number from ID: " + lastModuleId + ", numberPart: " + numberPart, e);
                    nextNum = 1;
                }
            }
        }
        return String.format("MO%04d", nextNum);
    }
    @Override
    public Modules updateModule(ModulesDto modulesDto) {
        // Step 1: Check if the module to update exists
        Optional<Modules> optionalModule = modulesRepository.findById(modulesDto.getId());
        if (optionalModule.isEmpty()) {
            return null; // Module not found
        }

        // Step 2: Check if the target project exists
        Optional<Project> optionalProject = projectRepository.findById(modulesDto.getProjectId());
        if (optionalProject.isEmpty()) {
            return null; // Project not found
        }

        Modules module = optionalModule.get();

        // Step 3: Check for name conflict in the same project (excluding this module)
        boolean nameConflict = modulesRepository.existsByModuleNameAndProject_Id(modulesDto.getModuleName(), modulesDto.getProjectId())
                && !module.getModuleName().equalsIgnoreCase(modulesDto.getModuleName());

        if (nameConflict) {
            return null; // Conflict: another module with the same name exists in this project
        }

        // Step 4: Update fields
        module.setModuleName(modulesDto.getModuleName());
        module.setProject(optionalProject.get());

        // Step 5: Save and return updated module
        return modulesRepository.save(module);
    }
    @Override
    public boolean deleteModuleById(Long id) {
        // Step 1: Check if the module exists
        Optional<Modules> optionalModule = modulesRepository.findById(id);
        if (optionalModule.isEmpty()) {
            return false; // Module not found
        }

        // Step 2: Check if any sub-modules exist for this module
        boolean hasSubModules = subModuleRepository.existsByModules_Id(id);
        if (hasSubModules) {
            return false; // Cannot delete if sub-modules are present
        }

        // Step 3: Safe to delete
        modulesRepository.deleteById(id);
        return true;
    }
    @Override
    public boolean checkIfModuleExistsById(Long id) {
        return modulesRepository.existsById(id);
    }


    @Override
    public boolean isDuplicateModuleNameForUpdate(Long currentModuleId, String newModuleName, Long projectId) {
        Optional<Modules> existing = modulesRepository.findByModuleNameIgnoreCaseAndProject_Id(newModuleName, projectId);
        return existing.isPresent() && !existing.get().getId().equals(currentModuleId);
    }

    @Override
    public java.util.List<ModulesDto> getAllModulesByProjectId(Long projectId) {
        java.util.List<Modules> modulesList = modulesRepository.findAllByProject_Id(projectId);
        java.util.List<ModulesDto> modulesDtoList = new java.util.ArrayList<>();
        for (Modules module : modulesList) {
            ModulesDto modulesdto = new ModulesDto();
            org.springframework.beans.BeanUtils.copyProperties(module, modulesdto);
            if (module.getProject() != null) {
                modulesdto.setProjectId(module.getProject().getId());
            }
            modulesDtoList.add(modulesdto);
        }
        return modulesDtoList;
    }

    @Override
    public ModulesDto getModuleById(Long moduleId) {
        Optional<Modules> moduleOpt = modulesRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            return null;
        }
        Modules module = moduleOpt.get();
        ModulesDto modulesdto = new ModulesDto();
        org.springframework.beans.BeanUtils.copyProperties(module, modulesdto);
        if (module.getProject() != null) {
            modulesdto.setProjectId(module.getProject().getId());
        }
        return modulesdto;
    }
}