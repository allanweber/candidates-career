package com.allanweber.candidates_career_recruiter.app.applications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ApplicationVacancy {

    private final String id;

    private final String name;
}
