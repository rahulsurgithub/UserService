package dev.project.userservice.unit;

import dev.project.userservice.dtos.UserDto;
import dev.project.userservice.models.Session;
import dev.project.userservice.models.SessionStatus;
import dev.project.userservice.models.User;
import dev.project.userservice.repositories.SessionRepository;
import dev.project.userservice.repositories.UserRepository;
import dev.project.userservice.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private dev.project.userservice.clients.KafkaProducerClient kafkaProducerClient;

    @Mock
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    public void login_wrongPassword_throws() {
        User u = new User(); u.setEmail("a@b.com"); u.setPassword("hashed");
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(u));
        when(bCryptPasswordEncoder.matches("p", "hashed")).thenReturn(false);

        Exception ex = assertThrows(RuntimeException.class, () -> authService.login("a@b.com", "p"));
        assertTrue(ex.getMessage().contains("Wrong username password"));
    }

    @Test
    public void login_success_returnsResponseWithCookieAndDto() {
        User u = new User(); u.setEmail("x@y.com"); u.setPassword("hashed");
        when(userRepository.findByEmail("x@y.com")).thenReturn(Optional.of(u));
        when(bCryptPasswordEncoder.matches("pw", "hashed")).thenReturn(true);

        ResponseEntity<UserDto> resp = authService.login("x@y.com", "pw");
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getHeaders().getFirst(org.springframework.http.HttpHeaders.SET_COOKIE));
        assertNotNull(resp.getBody());
        assertEquals("x@y.com", resp.getBody().getEmail());
    }

    @Test
    public void logout_notFound_returnsNull() {
        when(sessionRepository.findByTokenAndUser_Id("t", 3L)).thenReturn(Optional.empty());
        ResponseEntity<Void> resp = authService.logout("t", 3L);
        assertNull(resp);
    }

    @Test
    public void logout_success_returnsOk() {
        Session s = new Session() {{
            setToken("t");
            setSessionStatus(SessionStatus.ACTIVE);
            setExpiringAt(new Date());
        }};
        when(sessionRepository.findByTokenAndUser_Id("t", 4L)).thenReturn(Optional.of(s));
        ResponseEntity<Void> resp = authService.logout("t", 4L);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    public void signUp_savesAndSendsKafka() throws Exception {
        when(bCryptPasswordEncoder.encode("pw")).thenReturn("hashed");
        User saved = new User(); saved.setEmail("a@b.com"); saved.setPassword("hashed");
        when(userRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(saved);

        // ensure objectMapper serializes to a non-null string so kafka sendMessage gets a payload
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any())).thenReturn("{}");

        UserDto dto = authService.signUp("a@b.com", "pw");
        assertEquals("a@b.com", dto.getEmail());
        verify(kafkaProducerClient).sendMessage(org.mockito.ArgumentMatchers.eq("userSignUp"), org.mockito.ArgumentMatchers.anyString());
    }
}
