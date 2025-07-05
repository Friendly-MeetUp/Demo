package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.BenchDto;

import java.util.List;

public interface BenchService {

    BenchDto getBenchByUserFullName(String firstName, String lastName);

    List<BenchDto> getBenchesByAvailabilityGreaterThan(int availability);

    List<BenchDto> getBenchDetails();

    /**
     * Search benches dynamically based on optional filters.
     *
     * @param benchId     partial or full bench ID (optional)
     * @param availability minimum availability percentage (optional, will search for users with availability > value)
     * @param allocated    exact allocated value (optional)
     * @param firstName    exact first name (optional)
     * @param lastName     exact last name (optional)
     * @param designation  designation filter (optional)
     * @param startDate    start date filter in yyyy-MM-dd format (optional)
     * @param endDate      end date filter in yyyy-MM-dd format (optional)
     * @param availabilityGreaterThan if true, search for users with availability greater than the given value
     * @return list of filtered BenchDto records
     */
    List<BenchDto> searchBenches(String benchId, Integer availability, Integer allocated, String firstName, String lastName, String designation, String startDate, String endDate, boolean availabilityGreaterThan);


}
