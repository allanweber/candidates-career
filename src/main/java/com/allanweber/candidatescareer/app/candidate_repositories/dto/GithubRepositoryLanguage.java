package com.allanweber.candidatescareer.app.candidate_repositories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
public class GithubRepositoryLanguage {

    private final String name;

    private final Long size;

    private final BigDecimal proportion;
}
