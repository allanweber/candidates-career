package com.allanweber.candidatescareer.app.candidate.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document(collection = "candidate-resume")
@AllArgsConstructor
@Getter
@Builder
public class CandidateResume {
    @Id
    private final String id;

    @NotBlank
    private final String candidateId;

    @NotBlank
    private final String owner;

    @NotBlank
    private final String fileName;

    @NotBlank
    private final String fileExtension;

    private final Long fileSize;

    @NotBlank
    private final String file;
}
