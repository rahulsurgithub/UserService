package dev.project.userservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.project.userservice.dtos.SetUserRolesRequestDto;
import dev.project.userservice.dtos.UserDto;
import dev.project.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable("id") Long userId) {
        UserDto userDto = userService.getUserDetails(userId);

        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> all = userService.getAllUsers();
        return ResponseEntity.ok(all);
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<UserDto> setUserRoles(@PathVariable("id") Long userId, @RequestBody SetUserRolesRequestDto request) {

        UserDto userDto = userService.setUserRoles(userId, request.getRoleIds());

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId, @RequestBody JsonNode body) {
        try {
            JsonNode nodeToUse = body;
            if (body.isArray()) {
                if (body.size() == 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Empty array provided"));
                }
                nodeToUse = body.get(0);
            }

            UserDto dto = objectMapper.treeToValue(nodeToUse, UserDto.class);

            UserDto updated = userService.updateUser(userId, dto);
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid request body", "details", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("id") Long userId) {
        boolean success = userService.deleteUser(userId);
        Map<String, String> resp = new HashMap<>();
        if (success) {
            resp.put("message", "User deleted successfully");
            return ResponseEntity.ok(resp);
        } else {
            resp.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        }
    }


}
