package com.fwd.tictactoe.repository;

import com.fwd.tictactoe.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
}
