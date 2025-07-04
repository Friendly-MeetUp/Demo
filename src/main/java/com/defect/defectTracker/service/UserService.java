package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUserDetails();
}
