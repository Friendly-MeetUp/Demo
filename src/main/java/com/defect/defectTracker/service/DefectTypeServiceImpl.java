package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.DefectTypeDto;
import com.defect.defectTracker.entity.DefectType;
import com.defect.defectTracker.repository.DefectTypeRepository;
import com.defect.defectTracker.service.DefectTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefectTypeServiceImpl implements DefectTypeService {

    private final DefectTypeRepository defectTypeRepository;

    // Create
    @Override
    public DefectTypeDto createDefectType(DefectTypeDto dto) {
        log.info("Creating DefectType: {}", dto.getDefectTypeName());

        String name = dto.getDefectTypeName().trim();
        if (defectTypeRepository.existsByDefectTypeNameIgnoreCase(name)) {
            throw new RuntimeException("A Defect Type with this name already exists. Please use a different name.");
        }

        DefectType entity = new DefectType();
        entity.setDefectTypeName(name);

        DefectType saved = defectTypeRepository.save(entity);
        dto.setId(saved.getId());

        log.info("DefectType created successfully with ID: {}", saved.getId());
        return dto;
    }

    // Update
    @Override
    public DefectTypeDto updateDefectType(Long id, DefectTypeDto dto) {
        log.info("Updating DefectType with ID: {}", id);

        if (id == null || id <= 0) {
            throw new RuntimeException("Please provide a valid Defect Type ID.");
        }

        String newName = dto.getDefectTypeName().trim();
        if (newName.isEmpty()) {
            throw new RuntimeException("Defect Type name cannot be empty. Please enter a valid name.");
        }

        DefectType existing = defectTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Defect Type found with the given ID."));

        boolean isDuplicate = !existing.getDefectTypeName().equalsIgnoreCase(newName)
                && defectTypeRepository.existsByDefectTypeNameIgnoreCase(newName);

        if (isDuplicate) {
            throw new RuntimeException("A Defect Type with this name already exists. Please use a different name.");
        }

        existing.setDefectTypeName(newName);
        DefectType updated = defectTypeRepository.save(existing);

        dto.setId(updated.getId());
        log.info("DefectType updated successfully with ID: {}", updated.getId());
        return dto;
    }

    // Get by ID
    @Override
    public DefectTypeDto getDefectTypeById(Long id) {
        log.info("Fetching DefectType by ID: {}", id);

        if (id == null || id <= 0) {
            throw new RuntimeException("Please provide a valid Defect Type ID.");
        }

        DefectType entity = defectTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Defect Type found with the given ID."));

        DefectTypeDto dto = new DefectTypeDto();
        dto.setId(entity.getId());
        dto.setDefectTypeName(entity.getDefectTypeName());

        return dto;
    }

    // Get All
    @Override
    public List<DefectTypeDto> getAllDefectTypes() {
        log.info("Fetching all DefectTypes");

        List<DefectType> entities = defectTypeRepository.findAll();
        if (entities.isEmpty()) {
            throw new RuntimeException("No Defect Types available to display.");
        }

        return entities.stream().map(entity -> {
            DefectTypeDto dto = new DefectTypeDto();
            dto.setId(entity.getId());
            dto.setDefectTypeName(entity.getDefectTypeName());
            return dto;
        }).collect(Collectors.toList());
    }

    // Delete
    @Override
    public void deleteDefectType(Long id) {
        log.info("Deleting DefectType with ID: {}", id);

        if (id == null || id <= 0) {
            throw new RuntimeException("Please provide a valid Defect Type ID.");
        }

        Optional<DefectType> optional = defectTypeRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Cannot delete. No Defect Type found with the specified ID.");
        }

        defectTypeRepository.deleteById(id);
        log.info("DefectType deleted successfully with ID: {}", id);
    }
}
