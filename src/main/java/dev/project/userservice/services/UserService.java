package dev.project.userservice.services;

import dev.project.userservice.dtos.UserDto;
import dev.project.userservice.models.Role;
import dev.project.userservice.models.User;
import dev.project.userservice.repositories.RoleRepository;
import dev.project.userservice.repositories.SessionRepository;
import dev.project.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SessionRepository sessionRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
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

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> dtos = new ArrayList<>();
        for (User u : users) {
            // ensure roles are loaded; map to DTO
            dtos.add(UserDto.from(u));
        }
        return dtos;
    }

    public UserDto updateUser(Long userId, UserDto dto) {
        Optional<User> userOptional = userRepository.findByIdWithRoles(userId);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            // if roles provided as Role objects use their ids to fetch from db and set
            Set<Role> newRoles = new HashSet<>();
            for (Role r : dto.getRoles()) {
                if (r != null && r.getId() != null) {
                    roleRepository.findById(r.getId()).ifPresent(newRoles::add);
                }
            }
            user.setRoles(newRoles);
        } else if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllByIdIn(new ArrayList<>(dto.getRoleIds()));
            user.setRoles(new HashSet<>(roles));
        }

        User saved = userRepository.save(user);
        return UserDto.from(saved);
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findByIdWithRoles(userId);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        // 1. delete sessions associated with user
        sessionRepository.deleteAllByUser_Id(userId);

        // 2. clear roles association in join table by clearing collection and saving
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            user.getRoles().clear();
            userRepository.save(user);
        }

        // 3. delete the user
        userRepository.deleteById(userId);
        return true;
    }
}
