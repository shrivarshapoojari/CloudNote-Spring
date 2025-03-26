package com.cloudnote.spring.demo.service;

import com.cloudnote.spring.demo.dto.UserDTO;
import com.cloudnote.spring.demo.model.User;

import java.util.List;

public interface UserService {
    void updateUserRole(Long userId, String roleName);

    List<User> getAllUsers();

    UserDTO getUserById(Long id);


    User findByUsername(String username);
}
