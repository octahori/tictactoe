package com.fwd.tictactoe.exception;

import com.fwd.tictactoe.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(GameException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleGame(GameException ex, HttpServletRequest req) {
        return new ErrorResponse(Instant.now(), req.getRequestURI(), ex.getMessage(), "GAME_NOT_FOUND");
    }

    @ExceptionHandler(InvalidMoveException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidMove(InvalidMoveException ex, HttpServletRequest req) {
        return new ErrorResponse(Instant.now(), req.getRequestURI(), ex.getMessage(), "INVALID_MOVE");
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalState(IllegalStateException ex, HttpServletRequest req) {
        return new ErrorResponse(Instant.now(), req.getRequestURI(), ex.getMessage(), "ILLEGAL_STATE");
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("Validation error");
        return new ErrorResponse(Instant.now(), req.getRequestURI(), message, "VALIDATION_ERROR");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOther(Exception ex, HttpServletRequest req) {
        return new ErrorResponse(Instant.now(), req.getRequestURI(), ex.getMessage(), "INTERNAL_ERROR");
    }
}
