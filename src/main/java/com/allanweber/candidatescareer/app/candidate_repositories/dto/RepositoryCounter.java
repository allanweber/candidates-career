package com.allanweber.candidatescareer.app.candidate_repositories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class RepositoryCounter {

    private long repositories;

    private long starts;

    private long watchers;

}
