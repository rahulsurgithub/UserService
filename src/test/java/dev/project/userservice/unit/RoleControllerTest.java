package dev.project.userservice.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.project.userservice.controllers.RoleController;
import dev.project.userservice.dtos.CreateRoleRequestDto;
import dev.project.userservice.models.Role;
import dev.project.userservice.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Role role1;
    private Role role2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();

        role1 = new Role();
        role1.setName("ADMIN");

        role2 = new Role();
        role2.setName("USER");
    }

    @Test
    void createRole_returnsRole() throws Exception {
        CreateRoleRequestDto dto = new CreateRoleRequestDto();
        dto.setName("ADMIN");

        when(roleService.createRole(eq("ADMIN"))).thenReturn(role1);

        mockMvc.perform(post("/roles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ADMIN"));

        verify(roleService, times(1)).createRole(eq("ADMIN"));
    }

    @Test
    void getAllRoles_returnsList() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(role1, role2));

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[1].name").value("USER"));

        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    void getRoleById_found_returnsRole() throws Exception {
        when(roleService.getRoleById(1L)).thenReturn(role1);

        mockMvc.perform(get("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ADMIN"));

        verify(roleService, times(1)).getRoleById(1L);
    }

    @Test
    void getRoleById_notFound_returns404() throws Exception {
        when(roleService.getRoleById(9L)).thenReturn(null);

        mockMvc.perform(get("/roles/9"))
                .andExpect(status().isNotFound());

        verify(roleService, times(1)).getRoleById(9L);
    }

    @Test
    void updateRole_found_returnsUpdated() throws Exception {
        CreateRoleRequestDto dto = new CreateRoleRequestDto();
        dto.setName("SUPERADMIN");

        Role updated = new Role();
        updated.setName("SUPERADMIN");

        when(roleService.updateRole(1L, "SUPERADMIN")).thenReturn(updated);

        mockMvc.perform(put("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERADMIN"));

        verify(roleService, times(1)).updateRole(1L, "SUPERADMIN");
    }

    @Test
    void updateRole_notFound_returns404() throws Exception {
        CreateRoleRequestDto dto = new CreateRoleRequestDto();
        dto.setName("X");

        when(roleService.updateRole(10L, "X")).thenReturn(null);

        mockMvc.perform(put("/roles/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        verify(roleService, times(1)).updateRole(10L, "X");
    }

    @Test
    void deleteRole_success_returnsOkWithMessage() throws Exception {
        when(roleService.deleteRole(1L)).thenReturn(true);

        mockMvc.perform(delete("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Role deleted successfully"));

        verify(roleService, times(1)).deleteRole(1L);
    }

    @Test
    void deleteRole_failure_returnsBadRequest() throws Exception {
        when(roleService.deleteRole(2L)).thenReturn(false);

        mockMvc.perform(delete("/roles/2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Role not found or could not be deleted"));

        verify(roleService, times(1)).deleteRole(2L);
    }
}
