package com.fwd.tictactoe.engine;

import com.fwd.tictactoe.domain.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuleEngineTest {

    private final RuleEngine ruleEngine = new RuleEngine();

    @Test
    @DisplayName("Detect horizontal win")
    void shouldDetectHorizontalWin() {
        Board board = new Board(3);
        board.setCell(0, 0, "X");
        board.setCell(0, 1, "X");
        board.setCell(0, 2, "X");

        boolean win = ruleEngine.isWinningMove(board, 0, 2, "X");
        assertThat(win).isTrue();
    }

    @Test
    @DisplayName("Detect vertical win")
    void shouldDetectVerticalWin() {
        Board board = new Board(3);
        board.setCell(0, 0, "O");
        board.setCell(1, 0, "O");
        board.setCell(2, 0, "O");

        boolean win = ruleEngine.isWinningMove(board, 2, 0, "O");
        assertThat(win).isTrue();
    }

    @Test
    @DisplayName("Detect diagonal win (top-left to bottom-right)")
    void shouldDetectDiagonalWin1() {
        Board board = new Board(3);
        board.setCell(0, 0, "X");
        board.setCell(1, 1, "X");
        board.setCell(2, 2, "X");

        boolean win = ruleEngine.isWinningMove(board, 2, 2, "X");
        assertThat(win).isTrue();
    }

    @Test
    @DisplayName("Detect diagonal win (bottom-left to top-right)")
    void shouldDetectDiagonalWin2() {
        Board board = new Board(3);
        board.setCell(2, 0, "O");
        board.setCell(1, 1, "O");
        board.setCell(0, 2, "O");

        boolean win = ruleEngine.isWinningMove(board, 0, 2, "O");
        assertThat(win).isTrue();
    }

    @Test
    @DisplayName("No win should return false")
    void shouldReturnFalseWhenNoWin() {
        Board board = new Board(3);
        board.setCell(0, 0, "X");
        board.setCell(1, 1, "O");
        board.setCell(0, 2, "X");

        boolean win = ruleEngine.isWinningMove(board, 0, 2, "X");
        assertThat(win).isFalse();
    }

    @Test
    @DisplayName("Detect board full")
    void shouldDetectBoardFull() {
        Board board = new Board(3);
        String[] symbols = {"X", "O"};
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board.setCell(i, j, symbols[index++ % 2]);
            }
        }
        assertThat(ruleEngine.isBoardFull(board)).isTrue();
    }

    @Test
    @DisplayName("Detect not board full")
    void shouldDetectNotBoardFull() {
        Board board = new Board(3);
        board.setCell(0, 0, "X");
        assertThat(ruleEngine.isBoardFull(board)).isFalse();
    }

    @Test
    @DisplayName("Detect win on 4x4 board")
    void shouldWinOn4x4() {
        Board board = new Board(4);
        for (int i = 0; i < 4; i++) {
            board.setCell(i, i, "O");
        }
        boolean win = ruleEngine.isWinningMove(board, 3, 3, "O");
        assertThat(win).isTrue();
    }

}
