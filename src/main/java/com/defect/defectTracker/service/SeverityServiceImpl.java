package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.SeverityDto;
import com.defect.defectTracker.entity.Severity;

import com.defect.defectTracker.repository.SeverityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@CrossOrigin("*")
@Service
@Transactional
public class SeverityServiceImpl implements SeverityService {
     Logger logger = LoggerFactory.getLogger(SeverityServiceImpl.class);

    @Autowired
    private SeverityRepository severityRepo;

    private SeverityDto mapToDto(Severity severity) {
        SeverityDto dto = new SeverityDto();
        dto.setId(severity.getId());
        dto.setName(severity.getSeverityName());
        dto.setColor(severity.getSeverityColor());
        return dto;
    }

    private Severity mapToEntity(SeverityDto dto) {
        Severity severity = new Severity();
        severity.setId(dto.getId());
        severity.setSeverityName(dto.getName());
        severity.setSeverityColor(dto.getColor());
        return severity;
    }

    @Override
    public SeverityDto createSeverity(SeverityDto severityDto) {
        logger.info("Creating severity with details: {}", severityDto);
        if (severityRepo.existsBySeverityName(severityDto.getName())) {
            logger.error("Severity name must be unique: {}", severityDto.getName());
            throw new IllegalArgumentException("Severity name must be unique");
        }
        Severity severity = mapToEntity(severityDto);
        Severity saved = severityRepo.save(severity);
        return mapToDto(saved);
    }

    @Override
    public List<SeverityDto> getAllSeverities() {
        logger.info("Fetching all severities");
        return severityRepo.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public SeverityDto updateSeverity(Long id, SeverityDto severityDto) {
        logger.info("Updating severity with ID: {} and details: {}", id, severityDto);
        if (severityRepo.existsByIdNotAndSeverityName(id, severityDto.getName())) {
            logger.error("Severity name must be unique: {}", severityDto.getName());
            throw new IllegalArgumentException("Severity name must be unique");
        }
        Optional<Severity> optionalSeverity = severityRepo.findById(id);
        if (optionalSeverity.isPresent()) {
            Severity severity = optionalSeverity.get();
            severity.setSeverityName(severityDto.getName());
            severity.setSeverityColor(severityDto.getColor());
            Severity updated = severityRepo.save(severity);
            return mapToDto(updated);
        }
        return null;
    }

    @Override
    public boolean deleteSeverity(Long id) {
        logger.info("Deleting severity with ID: {}", id);
        if (severityRepo.existsById(id)) {
            logger.info("Severity with ID: {} found, proceeding to delete", id);
            severityRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
