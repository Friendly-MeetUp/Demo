package com.defect.defectTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseTypeDto {
    private Long id;

    @NotBlank(message = "Release Type must not be blank")
    @Size(min = 3, max = 50, message = "Release Type must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Release Type must contain only letters and spaces")

    private String releaseTypeName;
}
