package com.allanweber.candidates_career_recruiter.app.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponse {

    private final ApplicationStatus status;

    private final String statusText;

    private final String extraDenyReason;

    private final String accessCode;

    private final String error;

    private final ApplicationVacancy vacancy;

    private final ApplicationCandidate candidate;

    private final LocalDateTime sent;

    private final LocalDateTime updated;
}
