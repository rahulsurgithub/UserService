package com.project.userservice.controller;

import com.project.userservice.dto.LoginDto;
import com.project.userservice.dto.UserDto;
import com.project.userservice.exceptions.NotFoundException;
import com.project.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userid}")
    public ResponseEntity<UserDto> getUser(@PathVariable("userid") Long id)  {
        UserDto dto = userService.getUser(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto created = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{userid}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("userid") Long id, @RequestBody UserDto userDto) {
        UserDto updated = userService.updateUser(id, userDto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userid}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("userid") Long id) {
        boolean ok = userService.deleteUser(id);
        Map<String, String> resp = new HashMap<>();
        if (ok) {
            resp.put("message", "User deleted successfully");
            return ResponseEntity.ok(resp);
        } else {
            resp.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        String token = userService.login(loginDto.email(), loginDto.password());
        Map<String, String> resp = new HashMap<>();
        if (token == null) {
            resp.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
        }
        resp.put("token", token);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        Map<String, String> resp = new HashMap<>();
        if (token == null) {
            resp.put("error", "token required");
            return ResponseEntity.badRequest().body(resp);
        }
        boolean ok = userService.logout(token);
        if (ok) {
            resp.put("message", "Logged out");
            return ResponseEntity.ok(resp);
        } else {
            resp.put("error", "session not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        }
    }
}
