package com.autoecole.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class WebSocketResponse<T> {

    @JsonProperty("type")
    private String type;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("data")
    private T data;

    @JsonProperty("message")
    private String message;

    public static <T> WebSocketResponse<T> notification(T data) {
        return WebSocketResponse.<T>builder()
                .type("NOTIFICATION")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static <T> WebSocketResponse<T> update(T data, String message) {
        return WebSocketResponse.<T>builder()
                .type("UPDATE")
                .timestamp(LocalDateTime.now())
                .data(data)
                .message(message)
                .build();
    }

    public static WebSocketResponse<Void> error(String message) {
        return WebSocketResponse.<Void>builder()
                .type("ERROR")
                .timestamp(LocalDateTime.now())
                .message(message)
                .build();
    }
}