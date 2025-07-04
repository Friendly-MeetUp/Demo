package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.UserDto;
import com.defect.defectTracker.entity.User;
import com.defect.defectTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserDto> getUserDetails(){
        List<User> userDetails = userRepository.findAll();

        List<UserDto> userDtoList = userDetails.stream()
                .map(user -> {
                    UserDto userDto = new UserDto();
                    userDto.setId(user.getId());
                    userDto.setFirstName(user.getFirstName());
                    userDto.setEmail(user.getEmail());
                    userDto.setLastName(user.getLastName());
                    return userDto;
                })
                .toList();

        return userDtoList;
    }
}
