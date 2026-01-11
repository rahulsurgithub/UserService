package com.project.userservice.service;

import com.project.userservice.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(int id);
}
