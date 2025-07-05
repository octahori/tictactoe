package com.fwd.tictactoe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateGameRequest {

    @Min(value = 3, message = "Board size must be at least 3")
    private int boardSize;

    @NotBlank(message = "First player symbol is required")
    private String firstPlayerSymbol;

}
