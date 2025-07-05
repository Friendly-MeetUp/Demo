package com.defect.defectTracker.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
@Entity
@Table(name = "ReleaseTestCase",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"release_id", "testcase_id"}, name = "uniquekeyReleaseTestCase"),
        })
public class ReleaseTestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String releaseTestCaseId;

    @Column(nullable = false)
    private Date testDate;

    @Column(nullable = false)
    private Time testTime;

    //change testCaseStatus to String for better readability
    @Column(nullable = false)
    private int  testCaseStatus;

    @Column(nullable = false)
    private String description;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id",nullable = false)
    private Releases releases;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id",nullable = false)
    private TestCase testCase;
}
