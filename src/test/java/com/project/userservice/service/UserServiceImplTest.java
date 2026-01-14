package com.project.userservice.service;

import com.project.userservice.dto.UserDto;
import com.project.userservice.entity.User;
import com.project.userservice.entity.Session;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private UserDto dto1;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "John", "Doe", "jdoe", "pass", "john@example.com", "123");
        user2 = new User(2L, "Jane", "Smith", "jsmith", "pass2", "jane@example.com", "456");
        dto1 = new UserDto("Alice", "Walker", "alice", "pw", "alice@example.com", "789");
    }

    @Test
    void createUser_shouldSaveAndReturnDto() {
        User saved = new User(10L, dto1.firstname(), dto1.lastname(), dto1.username(), dto1.password(), dto1.email(), dto1.phone());
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDto result = userService.createUser(dto1);

        assertNotNull(result);
        assertEquals(dto1.firstname(), result.firstname());
        assertEquals(dto1.lastname(), result.lastname());
        assertEquals(dto1.username(), result.username());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUser_whenFound_returnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        UserDto res = userService.getUser(1L);
        assertNotNull(res);
        assertEquals(user1.getFirstname(), res.firstname());
    }

    @Test
    void getUser_whenNotFound_returnsNull() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserDto res = userService.getUser(99L);
        assertNull(res);
    }

    @Test
    void getAllUsers_returnsAllDtos() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserDto> list = userService.getAllUsers();
        assertEquals(2, list.size());
        assertEquals(user1.getFirstname(), list.get(0).firstname());
    }

    @Test
    void updateUser_whenFound_updatesAndReturnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto upd = new UserDto("Updated", "User", "upduser", "pw2", "upd@example.com", "000");
        UserDto res = userService.updateUser(1L, upd);

        assertNotNull(res);
        assertEquals("Updated", res.firstname());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenNotFound_returnsNull() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        UserDto res = userService.updateUser(5L, dto1);
        assertNull(res);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_whenFound_returnsTrue() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        boolean ok = userService.deleteUser(1L);
        assertTrue(ok);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_whenNotFound_returnsFalse() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        boolean ok = userService.deleteUser(99L);
        assertFalse(ok);
        verify(userRepository, never()).deleteById(anyLong());
    }

    // New tests for authentication/session methods
    @Test
    void login_success_createsSessionAndReturnsToken() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1));
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> {
            Session s = invocation.getArgument(0);
            s.setSessionId(100); // simulate generated id
            return s;
        });

        String token = userService.login("john@example.com", "pass");
        assertNotNull(token);
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void login_wrongPassword_returnsNull() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1));

        String token = userService.login("john@example.com", "wrong");
        assertNull(token);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void login_userNotFound_returnsNull() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user2));

        String token = userService.login("noone@example.com", "pass");
        assertNull(token);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void logout_existingSession_deletesAndReturnsTrue() {
        Session s = new Session();
        s.setToken("tok");
        when(sessionRepository.findByToken("tok")).thenReturn(Optional.of(s));

        boolean ok = userService.logout("tok");
        assertTrue(ok);
        verify(sessionRepository, times(1)).delete(s);
    }

    @Test
    void logout_sessionNotFound_returnsFalse() {
        when(sessionRepository.findByToken("bad")).thenReturn(Optional.empty());

        boolean ok = userService.logout("bad");
        assertFalse(ok);
        verify(sessionRepository, never()).delete(any(Session.class));
    }
}
