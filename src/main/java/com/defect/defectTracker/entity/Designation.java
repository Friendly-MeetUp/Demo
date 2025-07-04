package com.defect.defectTracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import com.defect.defectTracker.entity.User;
import java.util.List;

@Data
@Entity
public class Designation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String designation;

    @OneToMany(mappedBy = "designation", fetch = FetchType.LAZY)
    private List<User> users;
}
