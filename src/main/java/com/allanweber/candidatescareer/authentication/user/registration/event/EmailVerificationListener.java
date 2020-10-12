package com.allanweber.candidatescareer.authentication.user.registration.event;

import com.allanweber.candidatescareer.authentication.user.registration.repository.Verification;
import com.allanweber.candidatescareer.app.email.EmailService;
import com.allanweber.candidatescareer.authentication.user.registration.service.VerificationService;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationListener implements ApplicationListener<UserRegistrationEvent> {

    public static final String REGISTRATION_VERIFY_EMAIL = "registration/verify/email";
    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String SUBJECT = "Candidates Career - verificar conta de email.";
    public static final String NAME = "$NAME";
    public static final String URL = "$URL";

    private final VerificationService verificationService;
    private final AppHostConfiguration appHostConfiguration;
    private final EmailService emailService;

    @Override
    public void onApplicationEvent(UserRegistrationEvent userRegistrationEvent) {
        Verification verification = verificationService.createVerification(userRegistrationEvent.getUser().getEmail());
        String verificationUrl = UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getBackEnd()))
                .path(REGISTRATION_VERIFY_EMAIL)
                .queryParam(ID, verification.getId())
                .queryParam(EMAIL, verification.getEmail())
                .build()
                .toUriString();
        String name = userRegistrationEvent.getUser().getFirstName();

        String emailTemplate = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/mail/registration.html"), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());

        String emailMessage = emailTemplate.replace(NAME, name).replace(URL, verificationUrl);

        emailService.sendHtmlTemplate(SUBJECT, emailMessage, userRegistrationEvent.getUser().getEmail());
    }
}
