package dev.project.userservice.unit;

import dev.project.userservice.dtos.UserDto;
import dev.project.userservice.models.Role;
import dev.project.userservice.models.User;
import dev.project.userservice.repositories.RoleRepository;
import dev.project.userservice.repositories.SessionRepository;
import dev.project.userservice.repositories.UserRepository;
import dev.project.userservice.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void getUserDetails_returnsDtoWhenPresent() {
        User u = new User(); u.setEmail("a@b.com");
        when(userRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(u));
        UserDto dto = userService.getUserDetails(1L);
        assertNotNull(dto);
        assertEquals("a@b.com", dto.getEmail());
    }

    @Test
    public void setUserRoles_updatesRoles() {
        User u = new User(); u.setEmail("x@y.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(u));
        Role r1 = new Role(); r1.setId(1L); r1.setName("admin");
        Role r2 = new Role(); r2.setId(2L); r2.setName("user");
        when(roleRepository.findAllByIdIn(List.of(1L,2L))).thenReturn(List.of(r1,r2));

        // ensure save returns the user (avoid mock returning null)
        when(userRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto dto = userService.setUserRoles(2L, List.of(1L,2L));
        assertNotNull(dto);
        assertTrue(dto.getRoleIds().contains(1L));
    }

    @Test
    public void getAllUsers_returnsList() {
        User u = new User(); u.setEmail("p@q.com");
        when(userRepository.findAll()).thenReturn(List.of(u));
        List<UserDto> all = userService.getAllUsers();
        assertEquals(1, all.size());
        assertEquals("p@q.com", all.get(0).getEmail());
    }

    @Test
    public void updateUser_updatesFieldsAndRoles() {
        User u = new User(); u.setEmail("old@e.com");
        when(userRepository.findByIdWithRoles(5L)).thenReturn(Optional.of(u));

        UserDto dto = new UserDto(); dto.setEmail("new@e.com");
        // Provide roleIds so updateUser will call findAllByIdIn
        dto.setRoleIds(Set.of(1L));
        when(userRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(u);
        Role r = new Role(); r.setId(1L); r.setName("r");
        when(roleRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(r));

        UserDto updated = userService.updateUser(5L, dto);
        assertNotNull(updated);
        assertEquals("new@e.com", updated.getEmail());
    }

    @Test
    public void deleteUser_cleansSessionsAndRoles() {
        User u = new User(); u.setEmail("to@delete.com");
        Set<Role> roles = new HashSet<>(); Role r = new Role(); r.setId(1L); roles.add(r); u.setRoles(roles);
        when(userRepository.findByIdWithRoles(6L)).thenReturn(Optional.of(u));

        boolean res = userService.deleteUser(6L);
        assertTrue(res);
        verify(sessionRepository).deleteAllByUser_Id(6L);
    }
}
