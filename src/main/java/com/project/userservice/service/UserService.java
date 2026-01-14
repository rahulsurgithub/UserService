package com.project.userservice.service;

import com.project.userservice.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto userDto);
    boolean deleteUser(Long id);

    // Authentication/session management
    String login(String email, String password);
    boolean logout(String token);
}
