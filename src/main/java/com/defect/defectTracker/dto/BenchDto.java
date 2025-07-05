package com.defect.defectTracker.dto;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BenchDto {
    private Long id;
    private String benchId;
    private int availability;
    private int allocated;
    private String user;

}