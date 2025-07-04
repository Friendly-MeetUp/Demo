package com.defect.defectTracker.service;
import com.defect.defectTracker.dto.ReleasesDto;
import com.defect.defectTracker.entity.Project;
import com.defect.defectTracker.entity.Releases;
import com.defect.defectTracker.repository.ProjectRepository;
import com.defect.defectTracker.repository.ReleasesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReleasesServiceImpl implements ReleasesService {

    private static final Logger logger = LoggerFactory.getLogger(ReleasesServiceImpl.class);




    @Autowired
    private ReleasesRepository releasesRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private String generateNextReleaseId() {

        Long maxId = releasesRepository.findMaxId();
        long nextNumericId = (maxId == null) ? 1 : maxId + 1;
        return String.format("RE%04d", nextNumericId);

    }

    @Override
    public Releases createRelease(ReleasesDto releaseDto) {
        // Validate input
        if (releaseDto.getReleaseName() == null || releaseDto.getReleaseName().trim().isEmpty()) {
            throw new IllegalArgumentException("Release name is required");
        }

        if (releaseDto.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID is required");
        }


        Optional<Releases> existingRelease = releasesRepository
                .findByReleaseNameAndProjectId(releaseDto.getReleaseName(), releaseDto.getProjectId());

        if (existingRelease.isPresent()) {
            throw new IllegalArgumentException(String.format(
                    "Release with name '%s' already exists in project %s",
                    releaseDto.getReleaseName(),
                    releaseDto.getProjectId()
            ));
        }

        Releases release = new Releases();
        release.setReleaseName(releaseDto.getReleaseName());
        release.setDescription(releaseDto.getDescription());
        release.setReleasedate(releaseDto.getReleaseDate());
        release.setReleaseType(releaseDto.getReleaseType());
        release.setReleaseStatus(releaseDto.getReleaseStatus());
        release.setReleaseId(generateNextReleaseId());
        Project project=new Project();
        project.setId(releaseDto.getProjectId());
release.setProject(project);
//        release.setProject(projectRepository.findById(releaseDto.getProjectId())
//                .orElseThrow(() -> new IllegalArgumentException(
//                        "Project not found with id: " + releaseDto.getProjectId()
//                )));

        return releasesRepository.save(release);
    }


    @Override
    public List<ReleasesDto> getReleasesByProjectId(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID is required");
        }



        // Verify project exists
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project not found with id: " + projectId);
        }

        if (projectRepository.findById(projectId).isEmpty()) {
            throw new IllegalArgumentException("Project not found with id: " + projectId);
        }



        // Get all releases for the project and map to DTOs
        return releasesRepository.findByProjectId(projectId).stream()
                .map(release -> {
                    ReleasesDto dto = new ReleasesDto();
                    dto.setId(release.getId());
                    dto.setReleaseId(release.getReleaseId());
                    dto.setReleaseName(release.getReleaseName());
                    dto.setDescription(release.getDescription());
                    dto.setReleaseDate(release.getReleasedate());
                    dto.setReleaseType(release.getReleaseType());
                    dto.setReleaseStatus(release.getReleaseStatus());
                    dto.setProjectId(projectId);
                    return dto;
                })
                .collect(Collectors.toList());
    }




}


