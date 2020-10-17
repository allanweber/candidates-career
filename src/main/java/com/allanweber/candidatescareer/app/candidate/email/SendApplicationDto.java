package com.allanweber.candidatescareer.app.candidate.email;

import com.allanweber.candidatescareer.app.vacancy.dto.Salary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class SendApplicationDto {
    private final String candidateName;

    private final String candidateEmail;

    private final String recruiterName;

    private final String vacancyName;

    private final List<String> vacancySkills;

    private final String candidateApplicationId;

    private final String accessCode;

    private final Salary salary;
}
