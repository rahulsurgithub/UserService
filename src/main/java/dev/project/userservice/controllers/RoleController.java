package dev.project.userservice.controllers;

import dev.project.userservice.dtos.CreateRoleRequestDto;
import dev.project.userservice.models.Role;
import dev.project.userservice.services.RoleService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@CacheConfig(cacheNames = "roles")
public class RoleController {
    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    @CacheEvict(allEntries = true)
    public ResponseEntity<Role> createRole(@RequestBody CreateRoleRequestDto request) {
        Role role = roleService.createRole(request.getName());
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @GetMapping
    @Cacheable(key = "'all'")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Cacheable(key = "#id")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CacheEvict(allEntries = true)
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody CreateRoleRequestDto request) {
        Role updated = roleService.updateRole(id, request.getName());
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(allEntries = true)
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        boolean deleted = roleService.deleteRole(id);
        if (deleted) {
            return new ResponseEntity<>("Role deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Role not found or could not be deleted", HttpStatus.BAD_REQUEST);
    }
}
