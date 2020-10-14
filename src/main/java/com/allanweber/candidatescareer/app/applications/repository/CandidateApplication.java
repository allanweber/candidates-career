package com.allanweber.candidatescareer.app.applications.repository;

import com.allanweber.candidatescareer.app.applications.dto.ApplicationStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Document(collection = "candidate-applications")
@RequiredArgsConstructor
@Getter
@With
@Builder
@EqualsAndHashCode
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

    private final ApplicationStatus status;

    private final String statusText;

    private final String extraDenyReason;

    private final String error;

    private final LocalDateTime sent;

    private final LocalDateTime updated;
}
