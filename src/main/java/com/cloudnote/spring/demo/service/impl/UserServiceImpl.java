package com.cloudnote.spring.demo.service.impl;

import com.cloudnote.spring.demo.Repository.PasswordResetTokenRepository;
import com.cloudnote.spring.demo.Repository.RoleRepository;
import com.cloudnote.spring.demo.Repository.UserRepository;
import com.cloudnote.spring.demo.dto.UserDTO;
import com.cloudnote.spring.demo.model.AppRole;
import com.cloudnote.spring.demo.model.PasswordResetToken;
import com.cloudnote.spring.demo.model.Role;
import com.cloudnote.spring.demo.model.User;
import com.cloudnote.spring.demo.service.TotpService;
import com.cloudnote.spring.demo.service.UserService;
import com.cloudnote.spring.demo.utils.EmailService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;


    @Autowired
    EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TotpService totpService;



    @Override
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        AppRole appRole = AppRole.valueOf(roleName);
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        userRepository.save(user);
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public UserDTO getUserById(Long id) {
//        return userRepository.findById(id).orElseThrow();
        User user = userRepository.findById(id).orElseThrow();
        return convertToDto(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {

        Optional<User> user=userRepository.findByEmail(email);
        return  user;
    }

    private UserDTO convertToDto(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.getTwoFactorSecret(),
                user.isTwoFactorEnabled(),
                user.getSignUpMethod(),
                user.getRole(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );
    }


    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
    @Override
    public void updatePassword(Long userId, String password) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update password");
        }
    }


    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonLocked(!lock);
        userRepository.save(user);
    }

    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setCredentialsNonExpired(!expire);
        userRepository.save(user);
    }
    @Override
    public void generatePasswordResetToken(String email)  throws Exception{
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new RuntimeException("User not found"));


        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        passwordResetTokenRepository.save(resetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(email,resetUrl);

    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        if (resetToken.isUsed())
            throw new RuntimeException("Password reset token has already been used");

        if (resetToken.getExpiryDate().isBefore(Instant.now()))
            throw new RuntimeException("Password reset token has expired");

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public void registerUser(User user){
        if(user.getPassword()!=null)
        {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    public GoogleAuthenticatorKey generate2FASecret(Long userId)
    {
        User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));

        GoogleAuthenticatorKey key=totpService.generateSecret();
        user.setTwoFactorSecret(key.getKey());
        userRepository.save(user);
        return key;

    }

    @Override
    public boolean validate2FACode(Long userId, int code)
    {

        User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        return  totpService.verifyCode(user.getTwoFactorSecret(),code);
    }

    @Override
    public void  enable2FA(Long userId)
    {
        User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disable2FA(Long userId)
     {
         User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
         user.setTwoFactorEnabled(false);
         userRepository.save(user);

     }

}


