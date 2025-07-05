package com.defect.defectTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriorityDto {
    private Long id;
    private String priority;
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid color format. Please use the hex format #RRGGBB.")
    private String color;
}
