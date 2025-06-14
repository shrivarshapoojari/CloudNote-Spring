package com.cloudnote.spring.demo.controller;
import com.cloudnote.spring.demo.Repository.RoleRepository;
import com.cloudnote.spring.demo.Repository.UserRepository;
import com.cloudnote.spring.demo.dto.*;
import com.cloudnote.spring.demo.model.Role;
import com.cloudnote.spring.demo.model.User;
import com.cloudnote.spring.demo.security.jwt.JwtUtils;
import com.cloudnote.spring.demo.service.TotpService;
import com.cloudnote.spring.demo.service.UserService;
import com.cloudnote.spring.demo.service.impl.UserDetailsImpl;
import com.cloudnote.spring.demo.utils.AuthUtil;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cloudnote.spring.demo.model.AppRole;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    TotpService totpService;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;
    @PostMapping("/public/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {

            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

//      set the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println(("----------------------------------------------------AUTH"));
        System.out.println(authentication);
        System.out.println(("----------------------------------------------------AUTH"));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();


        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Collect roles from the UserDetails
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Prepare the response body, now including the JWT token directly in the body
        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

        // Return the response entity with the JWT token included in the response body
        return ResponseEntity.ok(response);
    }


    @PostMapping("/public/signUp")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Role role;

        if (strRoles == null || strRoles.isEmpty()) {
            role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            String roleStr = strRoles.iterator().next();
            if (roleStr.equals("admin")) {

                return ResponseEntity.badRequest().body(new MessageResponse("Error: You cannot assign yourself an admin role!"));
            } else {
                role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            }

            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            user.setAccountExpiryDate(LocalDate.now().plusYears(1));
            user.setTwoFactorEnabled(false);
            user.setSignUpMethod("email");
        }
        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }



    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles
        );
        System.out.println("______________________________________________________________________________");
            System.out.println(response);
        System.out.println("______________________________________________________________________________");
        return ResponseEntity.ok().body(response);
    }





    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }



    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){
        try{
            userService.generatePasswordResetToken(email);
            return ResponseEntity.ok(new MessageResponse("Password reset email sent!"));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error sending password reset email"));
        }

    }


    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @RequestParam String newPassword) {

        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password reset successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }


    @PostMapping("/enable-2fa")
    public ResponseEntity<String>enable2FA() throws Exception {
        Long userId= authUtil.loggedInUserId();
        GoogleAuthenticatorKey secret=userService.generate2FASecret(userId);
        System.out.println("QR CODE____________________________________________________");
        String  qrCodeUrl= totpService.getQrCodeUrl(secret,userService.getUserById(userId).getUserName());
        System.out.println(qrCodeUrl);
       System.out.println("QR CODE____________________________________________________");

        return ResponseEntity.ok(qrCodeUrl);

    }


    @PostMapping("/disable-2fa")
    public ResponseEntity<String>disable2FA() throws Exception {
        Long userId= authUtil.loggedInUserId();
        userService.disable2FA(userId);


        return ResponseEntity.ok("2FA Disabled");

    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<String>verify2FA(@RequestParam int code) throws Exception
    {
        Long userId = authUtil.loggedInUserId();
        boolean isValid = userService.validate2FACode(userId, code);
        if (isValid)
        {
            userService.enable2FA(userId);
            return ResponseEntity.ok("2FA verified");
        } else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("INVALID 2FA CODE");
        }
    }




    @GetMapping("/user/2fa-status")
    public ResponseEntity<?>get2FAStatus() throws Exception {
        User user=authUtil.loggedInUser();

        if(user!=null)
        {
            return ResponseEntity.ok().body(Map.of("is2faEnabled",user.isTwoFactorEnabled()));
        }
        else{
            throw new Exception("User not found");
        }

    }

    @PostMapping("/public/verify-2fa-login")
    public ResponseEntity<String> verify2FALogin(@RequestParam int code,
                                                 @RequestParam String jwtToken
                                                 )
    {
        String username=jwtUtils.getUserNameFromJwtToken(jwtToken);
        User user=userService.findByUsername(username);

        boolean isValid = userService.validate2FACode(user.getUserId(), code);
        if (isValid)
        {

            return ResponseEntity.ok("2FA verified");
        } else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("INVALID 2FA CODE");
        }
    }






}
