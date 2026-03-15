package com.movietickets.dto;

public record LoginResponse(
        String token,
        UserResponse user
) {}