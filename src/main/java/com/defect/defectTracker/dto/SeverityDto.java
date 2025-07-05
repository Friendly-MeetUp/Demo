package com.defect.defectTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeverityDto {
    private Long id;
    private String name;
    private String color;


}
