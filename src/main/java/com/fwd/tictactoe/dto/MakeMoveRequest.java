package com.fwd.tictactoe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MakeMoveRequest {

    @Min(0)
    private int row;

    @Min(0)
    private int col;

    @NotBlank
    private String symbol;

    private boolean overwrite = false;

}
