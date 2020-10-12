package com.allanweber.candidatescareer.app.social.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class GitHubData {

    private String login;

    private String name;

    private String location;

    private String bio;

    private String company;

    @JsonProperty("avatar_url")
    private String imageUrl;

    @JsonProperty("url")
    private String apiProfile;

    @JsonProperty("html_url")
    private String githubProfile;

    private long followers;

    private long following;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
