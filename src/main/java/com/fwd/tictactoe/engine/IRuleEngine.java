package com.fwd.tictactoe.engine;

import com.fwd.tictactoe.domain.Board;

public interface IRuleEngine {

    boolean isWinningMove(Board board, int row, int col, String symbol);

    boolean isBoardFull(Board board);

}
