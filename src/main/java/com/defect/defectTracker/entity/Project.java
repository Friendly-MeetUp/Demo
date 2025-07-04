package com.defect.defectTracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@Entity
@ToString
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique=true)
    private String projectId;

    @Column(nullable = false, unique=true)
    private String projectName;

    @Column(nullable = false)
    private String description;

    @Column(nullable=false)
    private Date startDate;

    @Column(nullable=false)
    private Date endDate;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable=false)
    private String clientName;

    @Column(nullable=false)
    private String country;

    @Column(nullable=false)
    private String state;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String phoneNo;
}
