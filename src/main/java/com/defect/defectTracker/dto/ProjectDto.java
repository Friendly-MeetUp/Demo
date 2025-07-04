package com.defect.defectTracker.dto;

import com.defect.defectTracker.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectDto {
    private Long id;
    private String projectId;
    private String projectName;
    private String description;
    private Date startDate;
    private Date endDate;
    private String clientName;
    private String country;
    private String state;
    private String email;
    private String phoneNo;
    private Long userId;

    // extra fields for user details _ Rishaban
    private String userFirstName;
    private String userLastName;


}