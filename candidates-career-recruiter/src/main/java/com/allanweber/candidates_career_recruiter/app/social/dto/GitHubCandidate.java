package com.allanweber.candidates_career_recruiter.app.social.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitHubCandidate {

    private long followers;

    private long following;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
