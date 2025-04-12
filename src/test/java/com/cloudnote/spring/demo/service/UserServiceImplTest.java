package com.cloudnote.spring.demo.service;

import com.cloudnote.spring.demo.Repository.PasswordResetTokenRepository;
import com.cloudnote.spring.demo.Repository.RoleRepository;
import com.cloudnote.spring.demo.Repository.UserRepository;
import com.cloudnote.spring.demo.dto.UserDTO;
import com.cloudnote.spring.demo.model.AppRole;
import com.cloudnote.spring.demo.model.PasswordResetToken;
import com.cloudnote.spring.demo.model.Role;
import com.cloudnote.spring.demo.model.User;
import com.cloudnote.spring.demo.service.TotpService;
import com.cloudnote.spring.demo.service.impl.UserServiceImpl;
import com.cloudnote.spring.demo.utils.EmailService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TotpService totpService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUserName("testUser");
        testUser.setEmail("testuser@example.com");

        testRole = new Role();
        testRole.setRoleName(AppRole.ROLE_USER);

        lenient().when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        lenient().when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
    }

    @Test
    void testUpdateUserRole() {
        when(roleRepository.findByRoleName(AppRole.ROLE_USER)).thenReturn(Optional.of(testRole));

        userService.updateUserRole(testUser.getUserId(), "ROLE_USER");

        verify(userRepository, times(1)).save(testUser);
        assertEquals(testRole, testUser.getRole());
    }

    @Test
    void testGetAllUsers() {
        userService.getAllUsers();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserById(testUser.getUserId());

        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
    }

    @Test
    void testFindByEmail() {
        Optional<User> result = userService.findByEmail(testUser.getEmail());

        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void testUpdatePassword() {
        String newPassword = "newPassword123";
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        userService.updatePassword(testUser.getUserId(), newPassword);

        verify(userRepository, times(1)).save(testUser);
        assertEquals("encodedPassword", testUser.getPassword());
    }

    @Test
    void testUpdateAccountLockStatus() {
        userService.updateAccountLockStatus(testUser.getUserId(), true);

        verify(userRepository, times(1)).save(testUser);
        assertFalse(testUser.isAccountNonLocked());
    }

    @Test
    void testUpdateAccountExpiryStatus() {
        userService.updateAccountExpiryStatus(testUser.getUserId(), true);

        verify(userRepository, times(1)).save(testUser);
        assertFalse(testUser.isAccountNonExpired());
    }

    @Test
    void testUpdateAccountEnabledStatus() {
        userService.updateAccountEnabledStatus(testUser.getUserId(), false);

        verify(userRepository, times(1)).save(testUser);
        assertFalse(testUser.isEnabled());
    }

    @Test
    void testUpdateCredentialsExpiryStatus() {
        userService.updateCredentialsExpiryStatus(testUser.getUserId(), true);

        verify(userRepository, times(1)).save(testUser);
        assertFalse(testUser.isCredentialsNonExpired());
    }





    @Test
    void testRegisterUser() {
        User newUser = new User();
        newUser.setPassword("password123");

        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");

        userService.registerUser(newUser);

        verify(userRepository, times(1)).save(newUser);
        assertEquals("encodedPassword", newUser.getPassword());
    }



    @Test
    void testValidate2FACode() {
        int code = 123456;

        // Make sure testUser has a non-null secret
        testUser.setTwoFactorSecret("testSecret");

        // Mock repository call to return the user
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));

        // Match the actual secret string used in the service
        when(totpService.verifyCode(eq("testSecret"), eq(code))).thenReturn(true);

        boolean result = userService.validate2FACode(testUser.getUserId(), code);

        assertTrue(result);
    }


    @Test
    void testEnable2FA() {
        userService.enable2FA(testUser.getUserId());

        verify(userRepository, times(1)).save(testUser);
        assertTrue(testUser.isTwoFactorEnabled());
    }

    @Test
    void testDisable2FA() {
        userService.disable2FA(testUser.getUserId());

        verify(userRepository, times(1)).save(testUser);
        assertFalse(testUser.isTwoFactorEnabled());
    }
}
