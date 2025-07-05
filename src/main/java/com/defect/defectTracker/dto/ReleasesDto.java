package com.defect.defectTracker.dto;

import com.defect.defectTracker.entity.Project;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data

public class ReleasesDto {


    private Long id;
    private String releaseId;
    private String description;
    private String releaseName;
    private Date releaseDate;
    private String releaseType;
    private String releaseStatus;
    private Long projectId;
}
