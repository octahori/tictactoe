package com.fwd.tictactoe.dto;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        String path,
        String message,
        String errorCode
) {
}
