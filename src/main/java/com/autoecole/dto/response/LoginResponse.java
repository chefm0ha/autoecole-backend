package com.autoecole.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        @JsonProperty("message") String message,
        @JsonProperty("email") String email,
        @JsonProperty("role") String role
) {}