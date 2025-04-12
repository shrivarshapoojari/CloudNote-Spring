package com.cloudnote.spring.demo.repository;


import com.cloudnote.spring.demo.Repository.UserRepository;
import com.cloudnote.spring.demo.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and find by username")
    void testFindByUsername()
    {
        User user = new User("shrivarsha", "shrivarsha@gmail.com", "password123");
        userRepository.save(user);

        Optional<User> found=userRepository.findByUserName("shrivarsha");
        assertTrue(found.isPresent());
    }
}
