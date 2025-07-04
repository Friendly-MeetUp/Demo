package com.defect.defectTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleDto {
    private Long id;
    @NotBlank(message = "roleName must not be blank")
    @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Role name must contain only letters and spaces")
    private String roleName;
}


