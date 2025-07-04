package com.defect.defectTracker.dto;

import com.defect.defectTracker.entity.Releases;
import com.defect.defectTracker.entity.TestCase;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class ReleaseTestCaseDto {
    private Long id;
    private String releaseTestCaseId;
    private Date testDate;
    private Time testTime;
    private int testCaseStatus;
    private Long testCaseId;
    private TestCase testCase;
    private Releases releases;
    private String description;
    private Long priorityId;
    private Long defectStatusId;
}
