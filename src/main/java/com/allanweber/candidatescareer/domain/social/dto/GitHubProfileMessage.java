package com.allanweber.candidatescareer.domain.social.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GitHubProfileMessage {
    private String candidateId;

    private String apiProfile;

    private String token;
}
