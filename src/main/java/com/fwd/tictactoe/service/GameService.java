package com.fwd.tictactoe.service;

import com.fwd.tictactoe.domain.*;
import com.fwd.tictactoe.engine.IRuleEngine;
import com.fwd.tictactoe.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fwd.tictactoe.exception.GameException;
import com.fwd.tictactoe.exception.InvalidMoveException;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    private final IRuleEngine ruleEngine;

    public GameService(GameRepository gameRepository, IRuleEngine ruleEngine) {
        this.gameRepository = gameRepository;
        this.ruleEngine = ruleEngine;
    }

    public Game createGame(int boardSize, String firstPlayerSymbol) {
        log.info("Creating new game with boardSize={} and firstPlayerSymbol={}", boardSize, firstPlayerSymbol);
        Game game = new Game(boardSize, firstPlayerSymbol);
        Game savedGame = gameRepository.save(game);
        log.debug("GameId={} state after move: status={}, nextPlayer={}", savedGame.getId(), savedGame.getStatus(), savedGame.getCurrentPlayerSymbol());
        return savedGame;
    }

    public Optional<Game> getGame(UUID gameId) {
        return gameRepository.findById(gameId);
    }

    public Game makeMove(UUID gameId, Move move) {
        log.info("GameId={} - Player {} attempts move at ({}, {}) overwrite={}", gameId, move.getSymbol(), move.getRow(), move.getCol(), move.isOverwrite());
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameException("Game not found"));

        if (!game.getStatus().equals(GameStatus.IN_PROGRESS)) {
            log.warn("GameId={} already over, status={}", gameId, game.getStatus());
            throw new IllegalStateException("Game is already over");
        }

        if (!game.getCurrentPlayerSymbol().equals(move.getSymbol())) {
            log.warn("GameId={} wrong turn. Current player={}, attempted={}", gameId, game.getCurrentPlayerSymbol(), move.getSymbol());
            throw new InvalidMoveException("Not this player's turn");
        }

        Board board = game.getBoard();
        boolean cellEmpty = board.isCellEmpty(move.getRow(), move.getCol());
        if (!cellEmpty && !move.isOverwrite()) {
            throw new InvalidMoveException("Cell already occupied");
        }
        if (!cellEmpty && move.isOverwrite()) {
            if (game.getOverwriteRemaining(move.getSymbol()) <= 0) {
                throw new InvalidMoveException("Overwrite power-up already used");
            }
            if (board.getCell(move.getRow(), move.getCol()).equals(move.getSymbol())) {
                throw new InvalidMoveException("Cannot overwrite own symbol");
            }
            game.useOverwrite(move.getSymbol());

            board.setCell(move.getRow(), move.getCol(), move.getSymbol());

            if (ruleEngine.isWinningMove(board, move.getRow(), move.getCol(), move.getSymbol())) {
                game.setStatus(move.getSymbol().equals("X") ? GameStatus.X_WINS : GameStatus.O_WINS);
            } else if (ruleEngine.isBoardFull(board)) {
                game.setStatus(GameStatus.DRAW);
            } else {
                game.switchPlayer(move.getSymbol().equals("X") ? "O" : "X");
            }

            Game savedGame = gameRepository.save(game);
            log.debug("GameId={} state after move: status={}, nextPlayer={}", savedGame.getId(), savedGame.getStatus(), savedGame.getCurrentPlayerSymbol());
            return savedGame;
        }
        
        board.setCell(move.getRow(), move.getCol(), move.getSymbol());

        if (ruleEngine.isWinningMove(board, move.getRow(), move.getCol(), move.getSymbol())) {
            game.setStatus(move.getSymbol().equals("X") ? GameStatus.X_WINS : GameStatus.O_WINS);
        } else if (ruleEngine.isBoardFull(board)) {
            game.setStatus(GameStatus.DRAW);
        } else {
            game.switchPlayer(move.getSymbol().equals("X") ? "O" : "X");
        }

        Game savedGame = gameRepository.save(game);
        log.debug("GameId={} state after move: status={}, nextPlayer={}", savedGame.getId(), savedGame.getStatus(), savedGame.getCurrentPlayerSymbol());
        return savedGame;
    }
}
