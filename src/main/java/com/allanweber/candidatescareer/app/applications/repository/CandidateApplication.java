package com.allanweber.candidatescareer.app.applications.repository;

import com.allanweber.candidatescareer.app.applications.dto.CandidateApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Document(collection = "candidate-applications")
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CandidateApplication {
    @Id
    private final String id;

    @NotBlank
    private final String candidateId;

    @NotBlank
    private final String candidateName;

    @NotBlank
    private final String vacancyId;

    @NotBlank
    private final String owner;

    @NotBlank
    private final String accessCode;

    private CandidateApplicationStatus status;

    private String error;

    private LocalDateTime sent;

    private LocalDateTime updated;
}
