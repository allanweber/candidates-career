package com.allanweber.candidatescareer.domain.social.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class GitHubData {

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
}
