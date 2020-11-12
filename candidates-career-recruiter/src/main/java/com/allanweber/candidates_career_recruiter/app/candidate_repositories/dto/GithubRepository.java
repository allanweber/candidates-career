package com.allanweber.candidates_career_recruiter.app.candidate_repositories.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class GithubRepository {
    private String name;

    private String description;

    private boolean fork;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String cloneUrl;

    private String mainLanguage;

    private List<GithubRepositoryLanguage> languages;

    private long stars;

    private long watchers;

    private long commits;

    private long pulls;

    private long branches;
}
