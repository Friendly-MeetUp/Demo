package com.defect.defectTracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.lang.Module;

@Data
@Entity
public class SubModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String subModuleId;

    @Column(nullable = false)
    private String subModuleName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modules_id", nullable = false)
    private Modules modules;
}
