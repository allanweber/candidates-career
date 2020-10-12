package com.allanweber.candidatescareer.app.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateApplicationResponse {

    private CandidateApplicationStatus status;

    private String accessCode;

    private String error;

    private String vacancyId;

    private String vacancyName;

    private LocalDateTime sent;

    private LocalDateTime updated;
}
