package com.allanweber.candidatescareer.app.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VacancyApplicationResponse {

    private final CandidateApplicationStatus status;

    private final String error;

    private final String candidateId;

    private final String candidateName;

    private final LocalDateTime sent;

    private final LocalDateTime updated;
}
