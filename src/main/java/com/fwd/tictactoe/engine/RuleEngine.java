package com.fwd.tictactoe.engine;

import com.fwd.tictactoe.domain.Board;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RuleEngine implements IRuleEngine {

    private static final Logger log = LoggerFactory.getLogger(RuleEngine.class);

    public boolean isWinningMove(Board board, int row, int col, String symbol) {
        log.debug("Checking winning move for symbol {} at ({} , {})", symbol, row, col);
        return checkDirection(board, row, col, symbol, 1, 0)  // Horizontal
                || checkDirection(board, row, col, symbol, 0, 1)  // Vertical
                || checkDirection(board, row, col, symbol, 1, 1)  // Diagonal kanan bawah
                || checkDirection(board, row, col, symbol, 1, -1); // Diagonal kiri bawah
    }

    private boolean checkDirection(Board board, int row, int col, String symbol, int dx, int dy) {
        int count = 1;
        count += countSameSymbol(board, row, col, symbol, dx, dy);
        count += countSameSymbol(board, row, col, symbol, -dx, -dy);
        return count >= board.getSize(); // win condition = board size
    }

    private int countSameSymbol(Board board, int row, int col, String symbol, int dx, int dy) {
        int count = 0;
        int x = row + dx;
        int y = col + dy;
        while (x >= 0 && y >= 0 && x < board.getSize() && y < board.getSize()
                && symbol.equals(board.getCell(x, y))) {
            count++;
            x += dx;
            y += dy;
        }
        return count;
    }

    public boolean isBoardFull(Board board) {
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getCell(i, j).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

}
