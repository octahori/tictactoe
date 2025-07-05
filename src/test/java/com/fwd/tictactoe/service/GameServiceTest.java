package com.fwd.tictactoe.service;

import com.fwd.tictactoe.domain.*;
import com.fwd.tictactoe.engine.IRuleEngine;
import com.fwd.tictactoe.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import com.fwd.tictactoe.exception.InvalidMoveException;
import com.fwd.tictactoe.exception.GameException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameRepository gameRepository;
    private IRuleEngine ruleEngine;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameRepository = mock(GameRepository.class);
        ruleEngine = mock(IRuleEngine.class);
        gameService = new GameService(gameRepository, ruleEngine);
    }

    @Test
    @DisplayName("makeMove should switch player when game continues")
    void makeMoveShouldSwitchPlayer() {
        // given
        Game game = new Game(3, "X");
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(ruleEngine.isWinningMove(any(), anyInt(), anyInt(), anyString())).thenReturn(false);
        when(ruleEngine.isBoardFull(any())).thenReturn(false);
        when(gameRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Move move = new Move(0, 0, "X", false);

        // when
        Game updated = gameService.makeMove(id, move);

        // then
        assertThat(updated.getCurrentPlayerSymbol()).isEqualTo("O");
        verify(gameRepository).save(updated);
    }

    @Test
    @DisplayName("makeMove should set status to win when winningMove true")
    void makeMoveShouldSetWinStatus() {
        Game game = new Game(3, "X");
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(ruleEngine.isWinningMove(any(), anyInt(), anyInt(), anyString())).thenReturn(true);
        when(gameRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Move move = new Move(0, 0, "X", false);

        Game updated = gameService.makeMove(id, move);

        assertThat(updated.getStatus()).isEqualTo(GameStatus.X_WINS);
    }

    @Test
    @DisplayName("makeMove should throw when not player's turn")
    void makeMoveShouldThrowWhenWrongTurn() {
        Game game = new Game(3, "X");
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));

        Move move = new Move(0, 0, "O", false);

        assertThatThrownBy(() -> gameService.makeMove(id, move))
                .isInstanceOf(InvalidMoveException.class)
                .hasMessageContaining("Not this player's turn");
    }

    @Test
    @DisplayName("should throw when cell occupied and overwrite false")
    void shouldThrowWhenCellOccupiedNoOverwrite() {
        Game game = new Game(3, "X");
        game.getBoard().setCell(0, 0, "O"); // occupied by opponent
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        Move move = new Move(0, 0, "X", false);

        assertThatThrownBy(() -> gameService.makeMove(id, move))
                .isInstanceOf(InvalidMoveException.class)
                .hasMessageContaining("Cell already occupied");
    }

    @Test
    @DisplayName("overwrite should succeed and decrease remaining count")
    void overwriteShouldSucceed() {
        Game game = new Game(3, "X");
        game.getBoard().setCell(0, 0, "O");
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(ruleEngine.isWinningMove(any(), anyInt(), anyInt(), anyString())).thenReturn(false);
        when(ruleEngine.isBoardFull(any())).thenReturn(false);
        when(gameRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Move move = new Move(0, 0, "X", true);

        Game updated = gameService.makeMove(id, move);

        assertThat(updated.getOverwriteRemaining("X")).isZero();
        assertThat(updated.getBoard().getCell(0,0)).isEqualTo("X");
    }

    @Test
    @DisplayName("should throw when overwrite already used")
    void shouldThrowWhenOverwriteUsedUp() {
        Game game = new Game(3, "X");
        // simulate used power-up
        game.useOverwrite("X");
        game.getBoard().setCell(0, 0, "O");
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        Move move = new Move(0, 0, "X", true);

        assertThatThrownBy(() -> gameService.makeMove(id, move))
                .isInstanceOf(InvalidMoveException.class)
                .hasMessageContaining("Overwrite power-up already used");
    }

    @Test
    @DisplayName("should throw when trying to overwrite own symbol")
    void shouldThrowWhenOverwriteOwnSymbol() {
        Game game = new Game(3, "X");
        game.getBoard().setCell(0, 0, "X");
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        Move move = new Move(0, 0, "X", true);

        assertThatThrownBy(() -> gameService.makeMove(id, move))
                .isInstanceOf(InvalidMoveException.class)
                .hasMessageContaining("Cannot overwrite own symbol");
    }

    @Test
    @DisplayName("should set status DRAW when board full and no win")
    void shouldSetStatusDraw() {
        Game game = new Game(3, "X");
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(ruleEngine.isWinningMove(any(), anyInt(), anyInt(), anyString())).thenReturn(false);
        when(ruleEngine.isBoardFull(any())).thenReturn(true);
        when(gameRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Move move = new Move(0, 0, "X", false);
        Game updated = gameService.makeMove(id, move);

        assertThat(updated.getStatus()).isEqualTo(GameStatus.DRAW);
    }

    @Test
    @DisplayName("overwrite path should set status WIN when winning")
    void overwriteShouldSetStatusWin() {
        Game game = new Game(3, "X");
        game.getBoard().setCell(0, 0, "O");
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(ruleEngine.isWinningMove(any(), anyInt(), anyInt(), anyString())).thenReturn(true);
        when(gameRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Move move = new Move(0, 0, "X", true);
        Game updated = gameService.makeMove(id, move);

        assertThat(updated.getStatus()).isEqualTo(GameStatus.X_WINS);
    }

    @Test
    @DisplayName("overwrite path should set status DRAW when board full")
    void overwriteShouldSetStatusDraw() {
        Game game = new Game(3, "X");
        game.getBoard().setCell(0, 0, "O");
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(ruleEngine.isWinningMove(any(), anyInt(), anyInt(), anyString())).thenReturn(false);
        when(ruleEngine.isBoardFull(any())).thenReturn(true);
        when(gameRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Move move = new Move(0, 0, "X", true);
        Game updated = gameService.makeMove(id, move);

        assertThat(updated.getStatus()).isEqualTo(GameStatus.DRAW);
    }

    @Test
    @DisplayName("makeMove should throw GameException when game not found")
    void shouldThrowGameNotFound() {
        UUID id = UUID.randomUUID();
        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        Move move = new Move(0, 0, "X", false);
        assertThatThrownBy(() -> gameService.makeMove(id, move))
                .isInstanceOf(GameException.class)
                .hasMessageContaining("Game not found");
    }

    @Test
    @DisplayName("should throw when game already over")
    void shouldThrowWhenGameOver() {
        Game game = new Game(3, "X");
        game.setStatus(GameStatus.DRAW);
        UUID id = game.getId();
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));

        Move move = new Move(0, 0, "X", false);
        assertThatThrownBy(() -> gameService.makeMove(id, move))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Game is already over");
    }

    @Test
    @DisplayName("createGame should save via repository and return saved instance")
    void createGameShouldSave() {
        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
        when(gameRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Game created = gameService.createGame(3, "X");

        verify(gameRepository).save(captor.capture());
        assertThat(captor.getValue().getBoard().getSize()).isEqualTo(3);
        assertThat(created.getCurrentPlayerSymbol()).isEqualTo("X");
    }
}
