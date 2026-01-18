package dev.project.userservice.unit;

import dev.project.userservice.models.Role;
import dev.project.userservice.repositories.RoleRepository;
import dev.project.userservice.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role1;
    private Role role2;

    @BeforeEach
    void setUp() {
        role1 = new Role();
        role1.setName("ADMIN");

        role2 = new Role();
        role2.setName("USER");
    }

    @Test
    void createRole_savesAndReturnsRole() {
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role created = roleService.createRole("ADMIN");

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("ADMIN");

        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void getAllRoles_returnsList() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        List<Role> all = roleService.getAllRoles();

        assertThat(all).hasSize(2);
        assertThat(all).contains(role1, role2);

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void getRoleById_found() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));

        Role r = roleService.getRoleById(1L);

        assertThat(r).isNotNull();
        assertThat(r.getName()).isEqualTo("ADMIN");

        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void getRoleById_notFound() {
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        Role r = roleService.getRoleById(2L);

        assertThat(r).isNull();
        verify(roleRepository, times(1)).findById(2L);
    }

    @Test
    void updateRole_found_updatesAndReturns() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role1));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role updated = roleService.updateRole(1L, "SUPERADMIN");

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("SUPERADMIN");

        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void updateRole_notFound_returnsNull() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        Role updated = roleService.updateRole(99L, "X");

        assertThat(updated).isNull();
        verify(roleRepository, times(1)).findById(99L);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void deleteRole_success_returnsTrue() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        // no exception on delete
        doNothing().when(roleRepository).deleteById(1L);

        boolean result = roleService.deleteRole(1L);

        assertThat(result).isTrue();
        verify(roleRepository, times(1)).existsById(1L);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRole_notExists_returnsFalse() {
        when(roleRepository.existsById(5L)).thenReturn(false);

        boolean result = roleService.deleteRole(5L);

        assertThat(result).isFalse();
        verify(roleRepository, times(1)).existsById(5L);
        verify(roleRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteRole_deleteThrows_returnsFalse() {
        when(roleRepository.existsById(2L)).thenReturn(true);
        doThrow(new RuntimeException("db error")).when(roleRepository).deleteById(2L);

        boolean result = roleService.deleteRole(2L);

        assertThat(result).isFalse();
        verify(roleRepository, times(1)).existsById(2L);
        verify(roleRepository, times(1)).deleteById(2L);
    }
}
