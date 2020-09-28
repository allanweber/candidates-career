package com.allanweber.candidatescareer.domain.candidate.email;

import com.allanweber.candidatescareer.domain.email.EmailService;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import com.allanweber.candidatescareer.infrastructure.configuration.security.AppSecurityConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateRegisterEmailService {
    public static final String NAME = "$NAME";
    public static final String VACANCY_NAME = "$VACANCY_NAME";
    public static final String SKILLS = "$SKILLS";
    public static final String URL = "$URL";
    public static final String DENY_URL = "$DENY_URL";
    public static final String RECRUITER_NAME = "$RECRUITER_NAME";
    public static final String SUBJECT = "Candidates Career - Cadastrar Currículo";

    private final EmailService emailService;
    private final AppHostConfiguration appHostConfiguration;

    public void sendEmail(SendRegisterDto sendRegisterDto) {
        String accessUrl = String.format("%s/candidate-register/%s", appHostConfiguration.getBackEnd(), sendRegisterDto.getCandidateRegisterId());

        String denyUrl = String.format("%s/candidate-register/%s/deny", appHostConfiguration.getBackEnd(),sendRegisterDto.getCandidateRegisterId());

        String emailTemplate = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/mail/candidate_register.html"), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());

        String skillsFormatted = sendRegisterDto.getVacancySkills().stream().map(skill -> String.format("<li>%s</li>", skill)).collect(Collectors.joining());
        String emailMessage = emailTemplate.replace(NAME, sendRegisterDto.getCandidateName())
                .replace(RECRUITER_NAME, sendRegisterDto.getRecruiterName())
                .replace(VACANCY_NAME, sendRegisterDto.getVacancyName())
                .replace(SKILLS, skillsFormatted)
                .replace(URL, accessUrl)
                .replace(DENY_URL, denyUrl);

        emailService.sendHtmlTemplate(SUBJECT, emailMessage, sendRegisterDto.getCandidateEmail());
    }
}
