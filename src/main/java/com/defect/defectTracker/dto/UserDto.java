package com.defect.defectTracker.dto;

import com.defect.defectTracker.entity.Designation;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNo;
    private Date joinDate;
    private boolean userStatus;
    private Long designationId;
    private String designationName;
}
