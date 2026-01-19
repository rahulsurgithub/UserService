package dev.project.userservice.services;

import dev.project.userservice.dtos.UserDto;
import dev.project.userservice.models.Role;
import dev.project.userservice.models.User;
import dev.project.userservice.repositories.RoleRepository;
import dev.project.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserDto getUserDetails(Long userId) {
        // use repository method that fetches roles eagerly
        Optional<User> userOptional = this.userRepository.findByIdWithRoles(userId);

        if (userOptional.isEmpty()) {
            return null;
        }

        return UserDto.from(userOptional.get());
    }

    public UserDto setUserRoles(Long userId, List<Long> roleIds) {
        Optional<User> userOptional = userRepository.findById(userId);
        List<Role> roles = roleRepository.findAllByIdIn(roleIds);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        // Use a mutable set so Hibernate can manage and merge the collection
        Set<Role> roleSet = new HashSet<>(roles);
        user.setRoles(roleSet);

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }
}
