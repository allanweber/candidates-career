package com.allanweber.candidatescareer.domain.candidate.repository;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document(collection = "candidate-register")
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CandidateRegister {
    @Id
    private final String id;

    @NotBlank
    private final String candidateId;

    @NotBlank
    private final String vacancyId;

    @NotBlank
    private final String owner;

    @NotBlank
    private final String accessCode;

    private CandidateRegisterStatus status;

    private String error;
}
