package com.cloudnote.spring.demo.Repository;


import com.cloudnote.spring.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);

  Optional<User>findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByUserName(String username);


}

