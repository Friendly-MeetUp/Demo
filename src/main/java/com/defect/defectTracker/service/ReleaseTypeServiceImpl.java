package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.ReleaseTypeDto;
import com.defect.defectTracker.entity.ReleaseType;
import com.defect.defectTracker.repository.ReleaseTypeRepository;
import com.defect.defectTracker.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReleaseTypeServiceImpl implements ReleaseTypeService {

    @Autowired
    private ReleaseTypeRepository repository;

    @Override
    public ReleaseTypeDto createReleaseType(ReleaseTypeDto dto) {
        if (!Utils.notNullValidation(dto.getReleaseTypeName())) {
            throw new IllegalArgumentException("Release Type Name cannot be empty");
        }

        if (repository.existsByReleaseTypeName(dto.getReleaseTypeName())) {
            throw new IllegalArgumentException("Release Type already exists");
        }

        ReleaseType entity = new ReleaseType();
        entity.setReleaseTypeName(dto.getReleaseTypeName().trim());
        repository.save(entity);

        dto.setId(entity.getId());
        return dto;
    }

    @Override
    public List<ReleaseTypeDto> getAllReleaseTypes() {
        List<ReleaseType> entities = repository.findAll();
        List<ReleaseTypeDto> dtos = new ArrayList<>();

        for (ReleaseType entity : entities) {
            ReleaseTypeDto dto = new ReleaseTypeDto();
            dto.setId(entity.getId());
            dto.setReleaseTypeName(entity.getReleaseTypeName());
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public ReleaseTypeDto getReleaseTypeById(Long id) {
        ReleaseType entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Release Type not found"));

        ReleaseTypeDto dto = new ReleaseTypeDto();
        dto.setId(entity.getId());
        dto.setReleaseTypeName(entity.getReleaseTypeName());

        return dto;
    }

    @Override
    public ReleaseTypeDto updateReleaseType(Long id, ReleaseTypeDto dto) {
        ReleaseType entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Release Type not found"));

        entity.setReleaseTypeName(dto.getReleaseTypeName().trim());
        repository.save(entity);

        dto.setId(entity.getId());
        return dto;
    }

    @Override
    public boolean deleteReleaseType(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}

