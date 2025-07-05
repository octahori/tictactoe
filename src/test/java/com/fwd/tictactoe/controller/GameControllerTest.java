package com.fwd.tictactoe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwd.tictactoe.domain.Game;
import com.fwd.tictactoe.domain.GameStatus;
import com.fwd.tictactoe.dto.CreateGameRequest;
import com.fwd.tictactoe.dto.MakeMoveRequest;
import com.fwd.tictactoe.service.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @Test
    @DisplayName("POST /game should create game and return 201")
    void createGameEndpoint() throws Exception {
        Game game = new Game(3, "X");
        when(gameService.createGame(anyInt(), anyString())).thenReturn(game);

        CreateGameRequest req = new CreateGameRequest();
        req.setBoardSize(3);
        req.setFirstPlayerSymbol("X");

        mockMvc.perform(post("/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.board.size").value(3))
                .andExpect(jsonPath("$.data.currentPlayerSymbol").value("X"));
    }

    @Test
    @DisplayName("POST /game with invalid board size should return 400")
    void createGameValidationFail() throws Exception {
        CreateGameRequest req = new CreateGameRequest();
        req.setBoardSize(2); // invalid (<3)
        req.setFirstPlayerSymbol("X");

        mockMvc.perform(post("/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /game/{id}/move should make move and return game")
    void makeMoveEndpoint() throws Exception {
        Game game = new Game(3, "X");
        game.setStatus(GameStatus.IN_PROGRESS);
        UUID id = game.getId();
        when(gameService.makeMove(Mockito.eq(id), Mockito.any())).thenReturn(game);

        MakeMoveRequest req = new MakeMoveRequest();
        req.setRow(0);
        req.setCol(1);
        req.setSymbol("X");
        req.setOverwrite(false);

        mockMvc.perform(post("/game/" + id + "/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("POST /game/{id}/move with blank symbol returns 400")
    void makeMoveValidationFail() throws Exception {
        UUID id = UUID.randomUUID();
        MakeMoveRequest req = new MakeMoveRequest();
        req.setRow(0);
        req.setCol(0);
        req.setSymbol(""); // blank

        mockMvc.perform(post("/game/" + id + "/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
