package com.fwd.tictactoe.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GridUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serializeGrid(String[][] grid) {
        try {
            return objectMapper.writeValueAsString(grid);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize grid", e);
        }
    }

    public static String[][] deserializeGrid(String json) {
        try {
            return objectMapper.readValue(json, String[][].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize grid", e);
        }
    }
}
