package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.SubModuleDto;
import com.defect.defectTracker.entity.Modules;
import com.defect.defectTracker.entity.SubModule;
import com.defect.defectTracker.repository.ModulesRepository;
import com.defect.defectTracker.repository.SubModuleRepository;
import com.defect.defectTracker.utils.StandardResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubModuleServiceImpl implements SubModuleService {
    @Autowired
    private SubModuleRepository subModuleRepository;
    @Autowired
    private final ModulesRepository modulesRepository;

    @Override
    @Transactional
    public StandardResponse createSubModule(SubModuleDto subModuleDto) {
        if (subModuleDto.getSubModuleName() == null || subModuleDto.getModuleId() == null) {
            throw new RuntimeException("Missing parameter: subModuleName or moduleId");
        }

        if (subModuleRepository.existsBySubModuleName(subModuleDto.getSubModuleName())) {
            throw new RuntimeException("SubModule already exists");
        }

        Modules module = modulesRepository.findById(subModuleDto.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + subModuleDto.getModuleId()));

        String generatedId;
        long count = subModuleRepository.count() + 1;
        do {
            generatedId = String.format("SM%04d", count);
            count++;
        } while (subModuleRepository.existsBySubModuleId(generatedId));

        SubModule subModule = new SubModule();
        subModule.setSubModuleId(generatedId);
        subModule.setSubModuleName(subModuleDto.getSubModuleName());
        subModule.setModules(module);

        subModuleRepository.save(subModule);

        return new StandardResponse("success", "Created successfully", null, 2000);
    }

    @Override
    public List<SubModuleDto> getSubModulesByModuleId(Long moduleId) {
        Modules module = modulesRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module ID " + moduleId + " not found"));

        List<SubModule> subModules = subModuleRepository.findByModules_Id(moduleId);

        return subModules.stream().map(subModule -> {
            Long modId = null;
            if (subModule.getModules() != null && subModule.getModules().getModuleId() != null) {
                try {
                    modId = Long.parseLong(subModule.getModules().getModuleId().replaceAll("[^0-9]", ""));
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid moduleId: " + subModule.getModules().getModuleId());
                }
            }

            return new SubModuleDto(
                    subModule.getId(),
                    subModule.getSubModuleName(),
                    modId,
                    module.getModuleName()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteSubModuleBySubModuleId(Long subModuleId) {
        SubModule subModule = subModuleRepository.findById(subModuleId)
                .orElseThrow(() -> new RuntimeException("SubModule not found with id: " + subModuleId));

        subModule.setModules(null);
        subModuleRepository.delete(subModule);
    }

    @Override

    public StandardResponse updateSubModule(Long id, SubModuleDto dto) throws Exception {
        if (dto == null) throw new IllegalArgumentException("Request body is empty");

        if (!StringUtils.hasText(dto.getSubModuleName())) {
            throw new IllegalArgumentException("SubModule name cannot be empty");
        }

        SubModule existing = subModuleRepository.findById(id)
                .orElseThrow(() -> new Exception("SubModule not found with id: " + id));

        if (existing.getSubModuleName().equals(dto.getSubModuleName())) {
            throw new IllegalStateException("No changes found. SubModule name is already '" + dto.getSubModuleName() + "'");
        }

        existing.setSubModuleName(dto.getSubModuleName());

        subModuleRepository.save(existing);
        return null;
    }

}