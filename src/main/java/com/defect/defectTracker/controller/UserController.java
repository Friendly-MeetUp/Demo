package com.defect.defectTracker.controller;


import com.defect.defectTracker.dto.UserDto;
import com.defect.defectTracker.service.UserService;
import com.defect.defectTracker.utils.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public StandardResponse getUserDetails() {
        List<UserDto> userDto = userService.getUserDetails();
        if(userDto!= null){
            return new StandardResponse("success", "User details fetched successfully", userDto, 2000);
        } else {
            return new StandardResponse("error", "User not found", null, 4000);
        }
    }
}
