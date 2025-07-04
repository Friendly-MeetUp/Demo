package com.defect.defectTracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "GroupPrivilege",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"privilege_id", "role_id"}, name = "uniquekeyGroupPrivilege" ),
        })
public class GroupPrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "privilege_id",nullable = false)
    private Privilege privilege;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;
}
