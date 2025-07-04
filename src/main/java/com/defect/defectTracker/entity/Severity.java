package com.defect.defectTracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Severity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String severityName;

    @Column(unique = true)
    private String severityColor;
}


