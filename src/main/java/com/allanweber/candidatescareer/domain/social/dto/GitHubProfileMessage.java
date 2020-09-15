package com.allanweber.candidatescareer.domain.social.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@JsonPropertyOrder({ "candidateId", "apiProfile", "token" })
public class GitHubProfileMessage {

    private String user;

    private String candidateId;

    private String apiProfile;

    private String token;
}
