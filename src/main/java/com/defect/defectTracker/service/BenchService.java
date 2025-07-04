package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.BenchDto;

import java.util.List;

public interface BenchService {



    List<BenchDto> searchBenches(String benchId, Integer availability, Integer allocated, String firstName, String lastName, String designation, String startDate, String endDate, boolean availabilityGreaterThan);


}
