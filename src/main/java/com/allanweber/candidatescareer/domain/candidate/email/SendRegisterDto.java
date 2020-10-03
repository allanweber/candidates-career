package com.allanweber.candidatescareer.domain.candidate.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class SendRegisterDto {
    private final String candidateName;

    private final String candidateEmail;

    private final String recruiterName;

    private final String vacancyName;

    private final List<String> vacancySkills;

    private final String candidateRegisterId;

    private final String accessCode;
}
