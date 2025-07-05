package com.defect.defectTracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "releases")
public class Releases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String releaseId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String releaseName;

    @Column(nullable = false)
    private Date releasedate;

    @Column(nullable = false)
    private String releaseType;

    @Column(nullable = false)
    private String releaseStatus;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
