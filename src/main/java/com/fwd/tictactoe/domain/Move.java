package com.fwd.tictactoe.domain;

import lombok.Getter;

@Getter
public class Move {

    private final int row;

    private final int col;

    private final String symbol;

    private final boolean overwrite;

    public boolean isOverwrite() {
        return overwrite;
    }

    public Move(int row, int col, String symbol, boolean overwrite) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
        this.overwrite = overwrite;
    }

}
