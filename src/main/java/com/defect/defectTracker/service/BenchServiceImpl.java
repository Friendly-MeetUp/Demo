package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.BenchDto;
import com.defect.defectTracker.entity.Bench;
import com.defect.defectTracker.repository.BenchRepository;
import com.defect.defectTracker.repository.ProjectAllocationRepository;
import com.defect.defectTracker.entity.ProjectAllocation;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

@Service
public class BenchServiceImpl implements BenchService {

    @Autowired
    private BenchRepository benchRepository;

    @Autowired
    private ProjectAllocationRepository projectAllocationRepository;


    @Override
    public BenchDto getBenchByUserFullName(String firstName, String lastName) {
        return null;
    }

    @Override
    public List<BenchDto> getBenchesByAvailabilityGreaterThan(int availability) {
        return List.of();
    }

    @Override
    public List<BenchDto> getBenchDetails() {
        return List.of();
    }

    public List<BenchDto> searchBenches(String benchId, Integer availability, Integer allocated, String firstName, String lastName, String designation, String startDate, String endDate, boolean availabilityGreaterThan) {
        Specification<Bench> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (benchId != null && !benchId.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("benchId")), "%" + benchId.toLowerCase() + "%"));

            }
            if (availability != null) {

                predicates.add(cb.greaterThanOrEqualTo(root.get("availability"), availability));
            }
            if (allocated != null) {
                predicates.add(cb.equal(root.get("allocated"), allocated));
            }
            if (firstName != null && !firstName.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                predicates.add(cb.equal(cb.lower(userJoin.get("firstName")), firstName.toLowerCase()));
            }
            if (lastName != null && !lastName.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                predicates.add(cb.equal(cb.lower(userJoin.get("lastName")), lastName.toLowerCase()));
            }
            if (designation != null && !designation.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                Join<Object, Object> designationJoin = userJoin.join("designation");
                predicates.add(cb.equal(cb.lower(designationJoin.get("designation")), designation.toLowerCase()));
            }
            if (startDate != null && !startDate.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                predicates.add(cb.greaterThanOrEqualTo(userJoin.get("joinDate"), java.sql.Date.valueOf(startDate)));
            }
            if (endDate != null && !endDate.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                predicates.add(cb.lessThanOrEqualTo(userJoin.get("joinDate"), java.sql.Date.valueOf(endDate)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Bench> benches = benchRepository.findAll(spec);
        return benches.stream().map(this::toDto).toList();
    }

    public java.util.Map<String, Object> searchBenchesWithStatus(
            String benchId, Integer availability, Integer allocated, String firstName, String lastName, String designation, String startDate, String endDate, boolean availabilityGreaterThan) {
        Specification<Bench> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (benchId != null && !benchId.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("benchId")), "%" + benchId.toLowerCase() + "%"));
            }
            if (availability != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("availability"), availability));
            }
            if (allocated != null) {
                predicates.add(cb.equal(root.get("allocated"), allocated));
            }
            if (firstName != null && !firstName.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                predicates.add(cb.equal(cb.lower(userJoin.get("firstName")), firstName.toLowerCase()));
            }
            if (lastName != null && !lastName.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                predicates.add(cb.equal(cb.lower(userJoin.get("lastName")), lastName.toLowerCase()));
            }
            if (designation != null && !designation.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                Join<Object, Object> designationJoin = userJoin.join("designation");
                predicates.add(cb.equal(cb.lower(designationJoin.get("designation")), designation.toLowerCase()));
            }
            if (startDate != null && !startDate.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                predicates.add(cb.greaterThanOrEqualTo(userJoin.get("joinDate"), java.sql.Date.valueOf(startDate)));
            }
            if (endDate != null && !endDate.isEmpty()) {
                Join<Object, Object> userJoin = root.join("user");
                predicates.add(cb.lessThanOrEqualTo(userJoin.get("joinDate"), java.sql.Date.valueOf(endDate)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        List<Bench> benches = benchRepository.findAll(spec);
        List<java.util.Map<String, Object>> data = new ArrayList<>();
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Bench bench : benches) {
            var user = bench.getUser();
            java.util.List<ProjectAllocation> allocations = projectAllocationRepository.findByUserId(user.getId());
            Date latestEnd = null;
            String currentProjectName = null;
            for (ProjectAllocation alloc : allocations) {
                if (latestEnd == null || alloc.getEndDate().after(latestEnd)) {
                    latestEnd = alloc.getEndDate();
                }
                if (!today.before(alloc.getStartDate()) && !today.after(alloc.getEndDate())) {
                    currentProjectName = alloc.getProject().getProjectName();
                }
            }
            String availablePeriods = sdf.format(user.getJoinDate()) + " to " + (latestEnd != null ? sdf.format(latestEnd) : "now");
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("userId", user.getId());
            map.put("fullName", user.getFirstName() + " " + user.getLastName());
            map.put("designation", user.getDesignation() != null ? user.getDesignation().getDesignation() : null);
            map.put("availability", bench.getAvailability());
            map.put("availablePeriods", availablePeriods);
            map.put("currentProjectName", currentProjectName);
            data.add(map);
        }
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("status", "success");
        response.put("message", "Bench list fetched successfully");
        response.put("data", data);
        response.put("statusCode", 2000);
        return response;
    }



    private BenchDto toDto(Bench bench) {
        return new BenchDto(
                bench.getId(),
                bench.getBenchId(),
                bench.getAvailability(),
                bench.getAllocated(),
                bench.getUser().getLastName() != null && !bench.getUser().getLastName().isEmpty()
                        ? bench.getUser().getFirstName() + " " + bench.getUser().getLastName()
                        : bench.getUser().getFirstName()
        );
    }
}
