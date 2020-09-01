package com.allanweber.candidatescareer.domain.user.registration.event;

import com.allanweber.candidatescareer.domain.user.registration.VerificationService;
import com.allanweber.candidatescareer.domain.user.registration.repository.Verification;
import com.allanweber.candidatescareer.infrastructure.configuration.security.AppSecurityConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class EmailVerificationListener implements ApplicationListener<UserRegistrationEvent> {

    public static final String REGISTRATION_VERIFY_EMAIL = "registration/verify/email";
    public static final String ID = "id";
    public static final String EMAIL = "email";

    private final JavaMailSender mailSender;
    private final VerificationService verificationService;
    private final AppSecurityConfiguration applicationConfiguration;

    @Override
    public void onApplicationEvent(UserRegistrationEvent userRegistrationEvent) {
        Verification verification = verificationService.createVerification(userRegistrationEvent.getUser().getEmail());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("New account created");
        message.setFrom("activation@candidates-career.tech");

        String verificationUri = UriComponentsBuilder.newInstance()
                .uri(URI.create(applicationConfiguration.getVerificationHost()))
                .path(REGISTRATION_VERIFY_EMAIL)
                .queryParam(ID, verification.getId())
                .queryParam(EMAIL, verification.getEmail())
                .build()
                .toUriString();

        String emailText = String.format("Account activation link: %s", verificationUri);
        message.setText(emailText);
        message.setTo(userRegistrationEvent.getUser().getEmail());
        mailSender.send(message);
    }
}
