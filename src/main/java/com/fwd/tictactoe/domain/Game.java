package com.fwd.tictactoe.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Getter
@Entity
public class Game {

    @Id
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    private Board board;

    private String currentPlayerSymbol;

    @ElementCollection
    private Map<String, Integer> overwriteRemaining = new HashMap<>();

    @Setter
    @Enumerated(EnumType.STRING)
    private GameStatus status;

    public Game() {}

    public Game(int boardSize, String firstPlayerSymbol) {
        this.id = UUID.randomUUID();
        this.board = new Board(boardSize);
        this.currentPlayerSymbol = firstPlayerSymbol;
        this.overwriteRemaining.put("X", 1);
        this.overwriteRemaining.put("O", 1);
        this.status = GameStatus.IN_PROGRESS;
    }

    public int getOverwriteRemaining(String symbol) {
        return overwriteRemaining.getOrDefault(symbol, 0);
    }

    public void useOverwrite(String symbol) {
        overwriteRemaining.put(symbol, getOverwriteRemaining(symbol) - 1);
    }

    public void switchPlayer(String nextSymbol) {
        this.currentPlayerSymbol = nextSymbol;
    }

}
