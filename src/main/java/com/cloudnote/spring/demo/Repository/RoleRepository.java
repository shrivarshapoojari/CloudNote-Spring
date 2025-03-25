package com.cloudnote.spring.demo.Repository;

import com.cloudnote.spring.demo.model.AppRole;
import com.cloudnote.spring.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository  extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
