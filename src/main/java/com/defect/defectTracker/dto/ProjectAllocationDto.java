package com.defect.defectTracker.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ProjectAllocationDto {
    private Long id;
    private Long projectId;
    private Long userId;
    private Date startDate;
    private Date endDate;
    private Integer allocationPercentage;

    private String projectName;
    private String roleName;
    private String userFullName;
    private Long roleId;
    private Integer availability; // Remaining availability after allocation

}