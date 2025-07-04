package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.DefectDto;
import com.defect.defectTracker.utils.StandardResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import com.defect.defectTracker.dto.DefectDto;
import com.defect.defectTracker.utils.StandardResponse;
import org.springframework.transaction.annotation.Transactional;
import com.defect.defectTracker.dto.DefectDto;
import java.util.List;
import com.defect.defectTracker.dto.DefectDto;
import com.defect.defectTracker.utils.StandardResponse;

import com.defect.defectTracker.dto.DefectDto;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
//import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface DefectService {

    //_____
    Integer uploadDefects(MultipartFile file) throws IOException;
    void exportdefectIds(HttpServletResponse response) throws IOException;
    void exportDefects(HttpServletResponse response) throws IOException;
    StandardResponse createDefect(DefectDto defectDto);
    StandardResponse updateDefect(Long id, DefectDto defectDto);
    StandardResponse deleteDefectById(Long id);
    DefectDto getDefectById(Long id);
    List<DefectDto> getDefectsByProjectId(Long projectId);
    DefectDto getDefectsByReleaseTestCaseId(Long releaseTestCaseId);
    ResponseEntity<StandardResponse> filterDefects(DefectDto defectDto);

    //DefectDto getByReleaseTestCaseId(Long defectId);
    //DefectDto getById(Long defectId);


}
