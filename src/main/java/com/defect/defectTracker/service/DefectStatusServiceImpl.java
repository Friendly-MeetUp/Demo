package com.defect.defectTracker.service;


import com.defect.defectTracker.dto.DefectStatusDto;
import com.defect.defectTracker.entity.DefectStatus;
import com.defect.defectTracker.repository.DefectStatusRepository;
import com.defect.defectTracker.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefectStatusServiceImpl implements DefectStatusService {

    @Autowired
    private  DefectStatusRepository repository;


    @Override
    public DefectStatusDto createDefectStatus(DefectStatusDto dto) {

        if (!Utils.notNullValidation(dto.getDefectStatusName()) || !Utils.notNullValidation(dto.getColorCode())) {
            return null;
        }

        if (repository.findByDefectStatusName(dto.getDefectStatusName()).isPresent()) {
            return null;
        }

        if (repository.findByColorCode(dto.getColorCode()).isPresent()) {
            return null;
        }

        DefectStatus defectStatus = new DefectStatus();
        defectStatus.setDefectStatusName(dto.getDefectStatusName());
        defectStatus.setColorCode(dto.getColorCode());

        DefectStatus saved = repository.save(defectStatus);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public List<DefectStatusDto> getAllDefectStatuses() {
        return repository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public DefectStatusDto getDefectStatusById(Long id) {
        DefectStatus status = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Defect status not found"));
        return convertToDto(status);
    }

    @Override
    public DefectStatusDto updateDefectStatus(Long id, DefectStatusDto dto) {
        DefectStatus existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Defect status not found"));

        existing.setDefectStatusName(dto.getDefectStatusName());
        existing.setColorCode(dto.getColorCode());

        DefectStatus updated = repository.save(existing);
        return convertToDto(updated);
    }





    @Override
    public DefectStatusDto deleteDefectStatus(Long id) {
        DefectStatus status = repository.findById(id).orElse(null);
        if (status == null) {
            return null;
        }
        repository.deleteById(id);
        return convertToDto(status);
    }

    private DefectStatusDto convertToDto(DefectStatus defectStatus) {
        DefectStatusDto dto = new DefectStatusDto();
        dto.setId(defectStatus.getId());
        dto.setDefectStatusName(defectStatus.getDefectStatusName());
        dto.setColorCode(defectStatus.getColorCode());
        return dto;
    }
}

