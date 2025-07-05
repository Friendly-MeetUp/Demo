package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.BenchDto;
import com.defect.defectTracker.entity.Bench;
import com.defect.defectTracker.repository.BenchRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BenchServiceImpl implements BenchService {

    @Autowired
    private BenchRepository benchRepository;

    public List<BenchDto> searchBenches(String benchId, Integer availability, Integer allocated, String firstName, String lastName, String designation, String startDate, String endDate, boolean availabilityGreaterThan) {
        Specification<Bench> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (benchId != null && !benchId.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("benchId")), "%" + benchId.toLowerCase() + "%"));
            }
            if (availability != null) {
                // Always use greaterThanOrEqualTo for default search
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

    @Override
    public List<BenchDto> getBenchDetails() {
        List<Bench> benches = benchRepository.findAll();
        return benches.stream().map(this::toDto).toList();
    }

    @Override
    public BenchDto getBenchByUserFullName(String firstName, String lastName) {
        return benchRepository.findAll().stream()
                .filter(b -> b.getUser().getFirstName().equalsIgnoreCase(firstName)
                        && (lastName == null || b.getUser().getLastName().equalsIgnoreCase(lastName)))
                .findFirst()
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    public List<BenchDto> getBenchesByAvailabilityGreaterThan(int availability) {
        return benchRepository.findAll().stream()
                .filter(b -> b.getAvailability() >= availability)
                .map(this::toDto)
                .toList();
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