package com.fwd.tictactoe.domain;

import com.fwd.tictactoe.util.GridUtil;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Arrays;

@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private int size;

    @Lob
    private String gridJson;

    @Transient
    private String[][] grid;

    public Board() {}

    public Board(int size) {
        this.size = size;
        this.grid = new String[size][size];
        for (String[] row : grid) {
            Arrays.fill(row, "");
        }
        this.gridJson = GridUtil.serializeGrid(this.grid);
    }

    public boolean isCellEmpty(int row, int col) {
        ensureGridLoaded();
        return grid[row][col].isEmpty();
    }

    public void setCell(int row, int col, String symbol) {
        ensureGridLoaded();
        grid[row][col] = symbol;
        this.gridJson = GridUtil.serializeGrid(this.grid);
    }

    public String getCell(int row, int col) {
        ensureGridLoaded();
        return grid[row][col];
    }

    public String[][] getGrid() {
        ensureGridLoaded();
        return grid;
    }

    private void ensureGridLoaded() {
        if (this.grid == null && this.gridJson != null) {
            this.grid = GridUtil.deserializeGrid(this.gridJson);
        }
    }
}
