package com.project.userservice.controller;

import com.project.userservice.dto.UserDto;
import com.project.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private UserController controller;

    @BeforeEach
    void setup() {
        controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getUser_found_returnsOk() throws Exception {
        UserDto dto = new UserDto("John","Doe","jdoe","pw","john@example.com","123");
        when(userService.getUser(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname").value("John"));
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        when(userService.getUser(99L)).thenReturn(null);

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_returnsList() throws Exception {
        UserDto d1 = new UserDto("A","B","a","p","a@e","1");
        UserDto d2 = new UserDto("C","D","c","p2","c@e","2");
        when(userService.getAllUsers()).thenReturn(Arrays.asList(d1, d2));

        // use "/users" (no trailing slash) to reliably match the controller mapping in MockMvc standalone setup
        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createUser_returnsCreated() throws Exception {
        UserDto input = new UserDto("New","User","nu","pw","n@e","9");
        when(userService.createUser(any(UserDto.class))).thenReturn(input);

        String json = "{\"firstname\":\"New\",\"lastname\":\"User\",\"username\":\"nu\",\"password\":\"pw\",\"email\":\"n@e\",\"phone\":\"9\"}";

        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("nu"));
    }

    @Test
    void updateUser_found_returnsOk() throws Exception {
        UserDto upd = new UserDto("Up","Date","u","pw","u@e","7");
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(upd);

        String json = "{\"firstname\":\"Up\",\"lastname\":\"Date\",\"username\":\"u\",\"password\":\"pw\",\"email\":\"u@e\",\"phone\":\"7\"}";

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Up"));
    }

    @Test
    void updateUser_notFound_returns404() throws Exception {
        UserDto upd = new UserDto("X","Y","x","pw","x@e","0");
        when(userService.updateUser(eq(5L), any(UserDto.class))).thenReturn(null);

        String json = "{\"firstname\":\"X\",\"lastname\":\"Y\",\"username\":\"x\",\"password\":\"pw\",\"email\":\"x@e\",\"phone\":\"0\"}";

        mockMvc.perform(put("/users/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_found_returnsMessage() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void deleteUser_notFound_returnsError() throws Exception {
        when(userService.deleteUser(99L)).thenReturn(false);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }
}
