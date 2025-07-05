package com.fwd.tictactoe.controller;

import com.fwd.tictactoe.domain.Game;
import com.fwd.tictactoe.domain.Move;
import com.fwd.tictactoe.dto.CreateGameRequest;
import com.fwd.tictactoe.dto.SuccessResponse;
import com.fwd.tictactoe.dto.MakeMoveRequest;
import com.fwd.tictactoe.service.GameService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.time.Instant;
import java.net.URI;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponse> createGame(@Valid @RequestBody CreateGameRequest request) {
        Game game = gameService.createGame(request.getBoardSize(), request.getFirstPlayerSymbol());
        return ResponseEntity.created(URI.create("/game/" + game.getId()))
                .body(new SuccessResponse(Instant.now(), game, "GAME_CREATED"));
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<SuccessResponse> makeMove(@PathVariable UUID id, @Valid @RequestBody MakeMoveRequest request) {
        Move move = new Move(request.getRow(), request.getCol(), request.getSymbol(), request.isOverwrite());
        Game updatedGame = gameService.makeMove(id, move);
        return ResponseEntity.ok(new SuccessResponse(Instant.now(), updatedGame, "MOVE_APPLIED"));
    }
}
