package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.DesignationDto;
import com.defect.defectTracker.entity.Designation;
import com.defect.defectTracker.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DesignationServiceImpl implements DesignationService {
    Logger logger = LoggerFactory.getLogger(DesignationServiceImpl.class);

    @Autowired
    private DesignationRepository designationRepository;

    private DesignationDto mapToDto(Designation entity) {
        DesignationDto dto = new DesignationDto();
        dto.setId(entity.getId());
        dto.setName(entity.getDesignation());
        return dto;
    }

    private Designation mapToEntity(DesignationDto dto) {
        Designation entity = new Designation();
        entity.setId(dto.getId());
        entity.setDesignation(dto.getName());
        return entity;
    }

    @Override
    public DesignationDto createDesignation(DesignationDto dto) {
        logger.info("Creating designation with name: {}", dto.getName());
        if (designationRepository.existsByDesignation(dto.getName())) {
            logger.warn("Designation with name {} already exists.", dto.getName());
            throw new RuntimeException("Designation unique constraint violated: Designation with this name already exists");
        }
        Designation entity = mapToEntity(dto);
        entity.setId(null);
        Designation saved = designationRepository.save(entity);
        logger.info("Designation created successfully with id: {}", saved.getId());
        return mapToDto(saved);
    }

    @Override
    public DesignationDto updateDesignation(Long id, DesignationDto dto) {
        logger.info("Updating designation with ID: {} to name: {}", id, dto.getName());
        Designation entity = designationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Designation not found with ID: {}", id);
                    return new RuntimeException("Designation not found");
                });
        if (!entity.getDesignation().equals(dto.getName()) && designationRepository.existsByDesignation(dto.getName())) {
            logger.warn("Designation with name {} already exists.", dto.getName());
            throw new RuntimeException("Designation unique constraint violated: Designation with this name already exists");
        }
        entity.setDesignation(dto.getName());
        Designation saved = designationRepository.save(entity);
        logger.info("Designation updated successfully for id: {}", saved.getId());
        return mapToDto(saved);
    }

    @Override
    public DesignationDto getDesignationById(Long id) {
        logger.info("Fetching designation with ID: {}", id);
        return designationRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> {
                    logger.error("Designation not found with ID: {}", id);
                    return new RuntimeException("Designation not found");
                });
    }

    @Override
    public boolean deleteDesignation(Long id) {
        logger.info("Deleting designation with ID: {}", id);
        if (designationRepository.existsById(id)) {
            designationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<DesignationDto> getAllDesignations() {
        logger.info("Fetching all designations");
        return designationRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }
}