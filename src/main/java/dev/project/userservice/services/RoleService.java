package dev.project.userservice.services;

import dev.project.userservice.models.Role;
import dev.project.userservice.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(String name) {
        Role role = new Role();
        role.setName(name);

        return roleRepository.save(role);
    }

    // New: return all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // New: get role by id
    public Role getRoleById(Long id) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        return optionalRole.orElse(null);
    }

    // New: update role
    public Role updateRole(Long id, String name) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            role.setName(name);
            return roleRepository.save(role);
        }
        return null;
    }

    // New: delete role, return true if deleted
    public boolean deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            return false;
        }
        try {
            roleRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
