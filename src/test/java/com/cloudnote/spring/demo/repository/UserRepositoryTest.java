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
    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        User user = new User("varsha", "varsha@gmail.com", "password123");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("varsha@gmail.com");

        assertTrue(found.isPresent());
        assertEquals("varsha", found.get().getUserName());
    }

    @Test
    @DisplayName("Should return true if email exists")
    void testExistsByEmail() {
        User user = new User("aishu", "aishu@gmail.com", "pass");
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("aishu@gmail.com");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return true if username exists")
    void testExistsByUserName() {
        User user = new User("ravi", "ravi@gmail.com", "pass");
        userRepository.save(user);

        boolean exists = userRepository.existsByUserName("ravi");

        assertTrue(exists);
    }
}
