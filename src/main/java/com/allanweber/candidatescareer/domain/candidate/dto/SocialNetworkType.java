package com.allanweber.candidatescareer.domain.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialNetworkType {

    TWITTER("Twitter"),
    FACEBOOK("Facebook"),
    STACKOVERFLOW("Stackoverflow"),
    WEBSITE("Website");

    private final String type;
}
