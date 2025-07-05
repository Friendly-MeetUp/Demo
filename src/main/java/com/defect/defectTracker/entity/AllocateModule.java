package com.defect.defectTracker.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "AllocateModule",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"modules_id", "project_id"}, name = "uniquekeyAllocateModule"),
        })
public class AllocateModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "modules_id", nullable = false)
    private Modules modules;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "sub_module_id", nullable = false)
    private SubModule subModule;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

}
