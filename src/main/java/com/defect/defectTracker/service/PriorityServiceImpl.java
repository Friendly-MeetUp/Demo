package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.PriorityDto;
import com.defect.defectTracker.entity.Priority;
import com.defect.defectTracker.repository.PriorityRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriorityServiceImpl implements PriorityService {

    private final PriorityRepository priorityRepository;

    @Override
    public PriorityDto createPriority(PriorityDto dto) {
        // Normalize input to avoid duplicates (case insensitive)
        String normalizedPriorityName = dto.getPriority().trim().toLowerCase();
        String normalizedColor = dto.getColor().trim();

        log.info("Creating Priority with name: {} and color: {}", normalizedPriorityName, normalizedColor);

        // Check if the priority name or color already exists
        if (priorityRepository.existsByPriority(normalizedPriorityName)) {
            log.warn("Priority creation failed - Duplicate priority name: {}", normalizedPriorityName);
            throw new DataIntegrityViolationException("Priority name already exists");
        }
        if (priorityRepository.existsByColor(normalizedColor)) {
            log.warn("Priority creation failed - Duplicate color: {}", normalizedColor);
            throw new DataIntegrityViolationException("Priority color already exists");
        }

        // Create new priority
        Priority priority = new Priority();
        priority.setPriority(normalizedPriorityName);
        priority.setColor(normalizedColor);

        // Save to the database
        Priority savedPriority = priorityRepository.save(priority);
        dto.setId(savedPriority.getId());
        dto.setPriority(savedPriority.getPriority());
        dto.setColor(savedPriority.getColor());

        log.info("Priority created successfully with ID: {}", savedPriority.getId());
        return dto;
    }

    @Override
    public PriorityDto getPriorityById(Long id) {
        log.info("Fetching Priority with ID: {}", id);

        // Fetch the priority, throwing an exception if not found
        Priority priority = priorityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Priority not found"));

        // Convert to DTO and return
        PriorityDto dto = new PriorityDto();
        dto.setId(priority.getId());
        dto.setPriority(priority.getPriority());
        dto.setColor(priority.getColor());
        return dto;
    }

    @Override
    public PriorityDto updatePriority(Long id, PriorityDto dto) {
        log.info("Updating Priority with ID: {}", id);

        // Find the existing priority
        Priority priority = priorityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Priority not found for update: {}", id);
                    return new RuntimeException("Priority not found for update with ID: " + id);
                });

        // Normalize new values
        String normalizedNewPriorityName = dto.getPriority().trim().toLowerCase();
        String normalizedNewColor = dto.getColor().trim();

        // Check if the new name or color already exists (excluding the current priority)
        Optional<Priority> existingByName = priorityRepository.findByPriorityIgnoreCase(normalizedNewPriorityName);
        if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
            log.warn("Update failed - Duplicate priority name: {}", normalizedNewPriorityName);
            throw new RuntimeException("Priority name already exists");
        }
        Optional<Priority> existingByColor = priorityRepository.findByColorIgnoreCase(normalizedNewColor);
        if (existingByColor.isPresent() && !existingByColor.get().getId().equals(id)) {
            log.warn("Update failed - Duplicate priority color: {}", normalizedNewColor);
            throw new RuntimeException("Priority color already exists");
        }

        // Update the priority and save
        priority.setPriority(normalizedNewPriorityName);
        priority.setColor(normalizedNewColor);
        Priority updatedPriority = priorityRepository.save(priority);

        // Return the updated DTO
        dto.setId(updatedPriority.getId());
        dto.setPriority(updatedPriority.getPriority());
        dto.setColor(updatedPriority.getColor());

        log.info("Priority updated successfully with new name: {}", updatedPriority.getPriority());
        return dto;
    }

    @Override
    public void deletePriority(Long id) {
        log.info("Deleting Priority with ID: {}", id);

        // Check if the priority exists before deletion
        Priority priority = priorityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Priority not found"));

        // Delete
        priorityRepository.delete(priority);
        log.info("Priority deleted successfully with ID: {}", id);
    }

    @Override
    public List<PriorityDto> getAllPriorities() {
        log.info("Fetching all priorities from the database");

        // Fetch all priorities and convert to DTOs
        List<Priority> priorities = priorityRepository.findAll();
        List<PriorityDto> priorityDtos = new ArrayList<>();

        for (Priority priority : priorities) {
            PriorityDto dto = new PriorityDto();
            dto.setId(priority.getId());
            dto.setPriority(priority.getPriority());
            dto.setColor(priority.getColor());
            priorityDtos.add(dto);
        }

        return priorityDtos;
    }
}
