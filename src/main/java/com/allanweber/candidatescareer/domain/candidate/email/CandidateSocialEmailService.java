package com.allanweber.candidatescareer.domain.candidate.email;

import com.allanweber.candidatescareer.domain.auth.AuthService;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.email.EmailService;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import com.allanweber.candidatescareer.infrastructure.configuration.security.AppSecurityConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateSocialEmailService {

    public static final String NAME = "$NAME";
    public static final String URL = "$URL";
    public static final String DENY_URL = "$DENY_URL";
    public static final String RECRUITER_NAME = "$RECRUITER_NAME";
    public static final String SUBJECT = "Candidates Career - acesso ao %s.";

    private final EmailService emailService;
    private final AuthService authService;
    private final AppHostConfiguration appHostConfiguration;

    public void sendSocialAccess(CandidateResponse candidate, SocialNetworkType socialNetworkType) {
        String accessUrl = String.format("%s/social-authorization/%s/%s", appHostConfiguration.getBackEnd(),
                candidate.getId(), socialNetworkType.toString().toLowerCase(Locale.getDefault()));

        String denyUrl = String.format("%s/social-authorization/%s/%s/deny", appHostConfiguration.getBackEnd(),
                candidate.getId(), socialNetworkType.toString());

        String emailMessage;
        if (socialNetworkType.equals(SocialNetworkType.GITHUB)) {
            String emailTemplate = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream("/mail/github_access.html"), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining());
            emailMessage = emailTemplate.replace(NAME, candidate.getName())
                    .replace(RECRUITER_NAME, authService.getAuthUser().getName())
                    .replace(URL, accessUrl)
                    .replace(DENY_URL, denyUrl);

        } else {
            throw new NotImplementedException(socialNetworkType.toString());
        }

        emailService.sendHtmlTemplate(String.format(SUBJECT, socialNetworkType.toString()), emailMessage, candidate.getEmail());
    }
}
