package com.defect.defectTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DefectTypeDto {

    private Long id;

    @NotBlank(message = "Defect Type name cannot be empty. Please enter a valid name.")
    @Size(min = 3, max = 50, message = "Defect Type name must be between 3 and 50 characters.")
    @Pattern(regexp = "^[A-Za-z0-9\\s\\-()]+$", message = "Defect Type name can only contain letters, numbers, spaces, dashes, and brackets.")
    private String defectTypeName;
}