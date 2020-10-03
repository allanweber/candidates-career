package com.allanweber.candidatescareer.domain.candidate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateRegisterResponse {

    private CandidateRegisterStatus status;

    private String accessCode;

    private String error;

    private CandidateRegisterVacancy vacancy;
}
