package com.defect.defectTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.awt.*;

@Data
@Entity
public class Priority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique=true)
    private String priority;

    @Column(nullable = false, unique=true)
    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Invalid color format. Please use the hex format #RRGGBB.")
    private String color;
}
