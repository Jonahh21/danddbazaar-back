package com.dandbazaar.back.games;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dandbazaar.back.auth.entities.User;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<List<Game>> findByOwnerUser(User user);
}
