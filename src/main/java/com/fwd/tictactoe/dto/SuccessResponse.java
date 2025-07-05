package com.fwd.tictactoe.dto;

import java.time.Instant;

public record SuccessResponse(
        Instant timestamp,
        Object data,
        String message
) {
}
