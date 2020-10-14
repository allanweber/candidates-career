package com.allanweber.candidatescareer.app.applications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ApplicationCandidate {

    private final String id;

    private final String name;
}
