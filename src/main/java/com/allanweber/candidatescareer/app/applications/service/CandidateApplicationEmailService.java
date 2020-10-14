package com.allanweber.candidatescareer.app.applications.service;

import com.allanweber.candidatescareer.app.candidate.email.SendApplicationDto;
import com.allanweber.candidatescareer.app.email.EmailService;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateApplicationEmailService {
    public static final String NAME = "$NAME";
    public static final String VACANCY_NAME = "$VACANCY_NAME";
    public static final String SKILLS = "$SKILLS";
    public static final String URL = "$URL";
    public static final String RECRUITER_NAME = "$RECRUITER_NAME";
    public static final String ACCESS_CODE = "$ACCESS_CODE";
    public static final String SUBJECT = "Candidates Career - Aplicar para vaga";

    private final EmailService emailService;
    private final AppHostConfiguration appHostConfiguration;

    public void sendEmail(SendApplicationDto sendApplicationDto) {
        String accessUrl = String.format("%s/candidate-application/%s", appHostConfiguration.getBackEnd(), sendApplicationDto.getCandidateApplicationId());

        String emailTemplate = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/mail/candidate_application.html"), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());

        String skillsFormatted = sendApplicationDto.getVacancySkills().stream().map(skill -> String.format("<li>%s</li>", skill)).collect(Collectors.joining());
        String emailMessage = emailTemplate.replace(NAME, sendApplicationDto.getCandidateName())
                .replace(RECRUITER_NAME, sendApplicationDto.getRecruiterName())
                .replace(VACANCY_NAME, sendApplicationDto.getVacancyName())
                .replace(ACCESS_CODE, sendApplicationDto.getAccessCode())
                .replace(SKILLS, skillsFormatted)
                .replace(URL, accessUrl);

        emailService.sendHtmlTemplate(SUBJECT, emailMessage, sendApplicationDto.getCandidateEmail());
    }
}
