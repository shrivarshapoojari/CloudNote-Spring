package com.cloudnote.spring.demo.service;

import com.cloudnote.spring.demo.dto.UserDTO;
import com.cloudnote.spring.demo.model.User;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void updateUserRole(Long userId, String roleName);

    List<User> getAllUsers();

    UserDTO getUserById(Long id);

     Optional<User> findByEmail(String email);
    User findByUsername(String username);

    public void updatePassword(Long userId, String password);

    public void updateAccountLockStatus(Long userId, boolean lock);

    public void updateAccountExpiryStatus(Long userId, boolean expire);

    public void updateAccountEnabledStatus(Long userId, boolean enabled);

    public void updateCredentialsExpiryStatus(Long userId, boolean expire);
    void generatePasswordResetToken(String email) throws  Exception;

    void resetPassword(String token, String newPassword);

    void registerUser(User user);

    GoogleAuthenticatorKey generate2FASecret(Long userId);

    boolean validate2FACode(Long userId, int code);

    void  enable2FA(Long userId);

    void disable2FA(Long userId);
}
