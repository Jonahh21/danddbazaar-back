package com.dandbazaar.back.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dandbazaar.back.auth.entities.User;
import com.dandbazaar.back.common.reporegister.Registered;

@Registered(User.class)
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
