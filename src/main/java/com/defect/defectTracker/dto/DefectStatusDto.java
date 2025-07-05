package com.defect.defectTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DefectStatusDto {
    private Long id;
    @NotBlank(message = "defect Status Name must not be blank")
    @Size(min = 3, max = 50, message = "defect Status name must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "defect Status name must contain only letters and spaces")
    private String defectStatusName;
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid color format. Please use the hex format #RRGGBB.")

    private String colorCode;
}
