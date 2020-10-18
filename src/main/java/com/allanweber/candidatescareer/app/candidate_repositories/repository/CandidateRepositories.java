package com.allanweber.candidatescareer.app.candidate_repositories.repository;

import com.allanweber.candidatescareer.app.candidate_repositories.dto.GithubRepository;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "candidate-repository")
@RequiredArgsConstructor
@Getter
@With
@Builder
@EqualsAndHashCode
public class CandidateRepositories {
    @Id
    private final String id;

    private final String owner;

    private final String candidateId;

    private final List<GithubRepository> repositories;
}
