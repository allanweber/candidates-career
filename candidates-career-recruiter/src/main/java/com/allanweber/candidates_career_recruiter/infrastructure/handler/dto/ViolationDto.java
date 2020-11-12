package com.allanweber.candidates_career_recruiter.infrastructure.handler.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ViolationDto {

    private final String fieldName;

    private final String message;

    public static ViolationDto create(String fieldName, String message) {
        return new ViolationDto(fieldName, message);
    }
}
