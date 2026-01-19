package dev.project.userservice.unit;

import dev.project.userservice.controllers.AuthController;
import dev.project.userservice.dtos.LoginRequestDto;
import dev.project.userservice.dtos.LogoutRequestDto;
import dev.project.userservice.dtos.SignUpRequestDto;
import dev.project.userservice.dtos.UserDto;
import dev.project.userservice.models.SessionStatus;
import dev.project.userservice.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    public void login_returnsUserDto() {
        LoginRequestDto req = new LoginRequestDto() {
            public String getEmail() { return "a@b.com"; }
            public String getPassword() { return "p"; }
        };

        UserDto dto = new UserDto();
        dto.setEmail("a@b.com");

        when(authService.login("a@b.com", "p")).thenReturn(ResponseEntity.ok(dto));

        ResponseEntity<UserDto> resp = authController.login(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("a@b.com", resp.getBody().getEmail());
    }

    @Test
    public void logout_returnsOk() {
        LogoutRequestDto req = new LogoutRequestDto() {
            public String getToken() { return "tok"; }
            public Long getUserId() { return 1L; }
        };

        when(authService.logout("tok", 1L)).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<Void> resp = authController.logout(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNull(resp.getBody());
    }

    @Test
    public void create_callsSignUp_andReturnsUser() {
        SignUpRequestDto req = new SignUpRequestDto() {
            public String getEmail() { return "c@d.com"; }
            public String getPassword() { return "pw"; }
        };

        UserDto created = new UserDto();
        created.setEmail("c@d.com");

        when(authService.signUp("c@d.com", "pw")).thenReturn(created);

        ResponseEntity<UserDto> resp = authController.create(req);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("c@d.com", resp.getBody().getEmail());
    }

    @Test
    public void validate_returnsSessionStatus() {
        // prepare
        when(authService.validate("t", 2L)).thenReturn(SessionStatus.ACTIVE);

        // call
        ResponseEntity<SessionStatus> resp = authController.validateToken(new dev.project.userservice.dtos.ValidateTokenRequestDto() {
            public String getToken() { return "t"; }
            public Long getUserId() { return 2L; }
        });

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(SessionStatus.ACTIVE, resp.getBody());
    }
}
