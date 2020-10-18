package com.allanweber.candidatescareer.app.candidate_repositories.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("clone_url")
    private String cloneUrl;

    @JsonProperty("language")
    private String mainLanguage;

    private List<GithubRepositoryLanguage> languages;

    @JsonProperty("stargazers_count")
    private long stars;

    @JsonProperty("watchers_count")
    private long watchers;

    private long commits;

    private long pulls;

    private long branches;
}
