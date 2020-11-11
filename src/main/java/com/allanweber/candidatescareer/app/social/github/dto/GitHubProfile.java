package com.allanweber.candidatescareer.app.social.github.dto;

import com.allanweber.candidatescareer.app.social.dto.GitHubCandidate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
public class GitHubProfile {

    private String user;

    private String name;

    private String location;

    private String bio;

    private String company;

    private String imageBase64;

    private String apiProfile;

    private String githubProfile;

    private String token;

    private GitHubCandidate gitHubCandidate;
}
