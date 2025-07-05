package com.defect.defectTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DefectTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DefectTrackerApplication.class, args);
		System.out.println("Defect Tracker Application is running...");
	}

}
