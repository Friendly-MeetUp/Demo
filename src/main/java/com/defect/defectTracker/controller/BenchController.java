package com.defect.defectTracker.controller;


import com.defect.defectTracker.dto.BenchDto;
import com.defect.defectTracker.service.BenchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bench")
public class BenchController {

    @Autowired
    private BenchService benchService;


    @GetMapping("/search")
    public ResponseEntity<?> searchBenches(
            @RequestParam(required = false) String benchId,
            @RequestParam(required = false) Integer availability,
            @RequestParam(required = false) Integer allocated,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "eq") String availabilityType
    ) {
        boolean availabilityGreaterThanOrEqual = "gt".equalsIgnoreCase(availabilityType) || "gte".equalsIgnoreCase(availabilityType);
        List<BenchDto> results = benchService.searchBenches(benchId, availability, allocated, firstName, lastName, designation, startDate, endDate, availabilityGreaterThanOrEqual);

        if (results == null || results.isEmpty()) {
            return ResponseEntity.status(400).body("No matching bench users found for the given criteria.");
//            return ResponseEntity.status(404).body("No matching bench users found for the given criteria.");
        }
        java.util.Map<String, Object> response = ((com.defect.defectTracker.service.BenchServiceImpl) benchService)
                .searchBenchesWithStatus(benchId, availability, allocated, firstName, lastName, designation, startDate, endDate, availabilityGreaterThanOrEqual);
        return ResponseEntity.ok(response);
//        return ResponseEntity.ok(results);
    }

}
