package dev.project.userservice.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.project.userservice.controllers.UserController;
import dev.project.userservice.dtos.SetUserRolesRequestDto;
import dev.project.userservice.dtos.UserDto;
import dev.project.userservice.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void getUserDetails_returnsNotFoundWhenNull() {
        when(userService.getUserDetails(5L)).thenReturn(null);
        ResponseEntity<UserDto> resp = userController.getUserDetails(5L);
        assertEquals(404, resp.getStatusCodeValue());
    }

    @Test
    public void getAllUsers_returnsList() {
        UserDto u1 = new UserDto(); u1.setEmail("a@b.com");
        when(userService.getAllUsers()).thenReturn(List.of(u1));

        ResponseEntity<List<UserDto>> resp = userController.getAllUsers();
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals(1, resp.getBody().size());
        assertEquals("a@b.com", resp.getBody().get(0).getEmail());
    }

    @Test
    public void setUserRoles_returnsUpdated() {
        SetUserRolesRequestDto req = new SetUserRolesRequestDto();
        req.setRoleIds(List.of(1L,2L));
        UserDto updated = new UserDto(); updated.setEmail("x@y.com");

        when(userService.setUserRoles(2L, List.of(1L,2L))).thenReturn(updated);

        ResponseEntity<UserDto> resp = userController.setUserRoles(2L, req);
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("x@y.com", resp.getBody().getEmail());
    }

    @Test
    public void updateUser_handlesArrayAndObject() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        UserDto dto = new UserDto(); dto.setEmail("u@v.com");
        when(userService.updateUser(eq(3L), any(UserDto.class))).thenReturn(dto);

        // simulate array input - controller handles JsonNode and picks first element
        String jsonArray = mapper.writeValueAsString(List.of(dto));
        // use controller method directly by converting string to JsonNode
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(jsonArray);
        ResponseEntity<?> resp = userController.updateUser(3L, node);
        assertEquals(200, ((ResponseEntity)resp).getStatusCodeValue());
        // body is UserDto
        assertTrue(resp.getBody() instanceof UserDto);
        assertEquals("u@v.com", ((UserDto)resp.getBody()).getEmail());

        // simulate object input
        String jsonObj = mapper.writeValueAsString(dto);
        node = mapper.readTree(jsonObj);
        when(userService.updateUser(eq(4L), any(UserDto.class))).thenReturn(dto);
        resp = userController.updateUser(4L, node);
        assertEquals(200, ((ResponseEntity)resp).getStatusCodeValue());
    }

    @Test
    public void deleteUser_returnsMessages() {
        when(userService.deleteUser(9L)).thenReturn(true);
        when(userService.deleteUser(10L)).thenReturn(false);

        ResponseEntity<Map<String, String>> success = userController.deleteUser(9L);
        assertEquals(200, success.getStatusCodeValue());
        assertEquals("User deleted successfully", success.getBody().get("message"));

        ResponseEntity<Map<String, String>> fail = userController.deleteUser(10L);
        assertEquals(404, fail.getStatusCodeValue());
        assertEquals("User not found", fail.getBody().get("error"));
    }
}
