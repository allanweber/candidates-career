package com.allanweber.candidatescareer.app.candidate.email;

import com.allanweber.candidatescareer.authentication.auth.service.AuthService;
import com.allanweber.candidatescareer.app.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.app.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.app.email.EmailService;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
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
        String emailMessage;
        if (socialNetworkType.equals(SocialNetworkType.GITHUB)) {
            String emailTemplate = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream("/mail/github_access.html"), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining());
            String denyUrl = String.format("%s/social-authorization/%s/%s/deny", appHostConfiguration.getBackEnd(),
                    candidate.getId(), socialNetworkType.toString());
            String accessUrl = String.format("%s/social-authorization/%s/%s", appHostConfiguration.getBackEnd(),
                    candidate.getId(), socialNetworkType.toString().toLowerCase(Locale.getDefault()));
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
