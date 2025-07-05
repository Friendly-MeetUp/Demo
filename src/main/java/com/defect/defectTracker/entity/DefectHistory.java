package com.defect.defectTracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
@Entity
public class DefectHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date defectDate;

    @Column(nullable = false)
    private Time defectTime;

    @Column(nullable = false)
    private String previousStatus;

    @Column(nullable = false)
    private String assignedTo;

    @Column(nullable = false)
    private String assignedBy;

    @Column(nullable = false)
    private Long releaseId;

    @Column(nullable = false)
    private String defectStatus;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "defect_id",nullable = false)
    private Defect defect;
}
