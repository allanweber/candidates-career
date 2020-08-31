package com.allanweber.candidatescareer.domain.user.registration.event;

import com.allanweber.candidatescareer.domain.user.dto.UserDto;
import com.allanweber.candidatescareer.domain.user.registration.VerificationService;
import com.allanweber.candidatescareer.domain.user.registration.repository.Verification;
import com.allanweber.candidatescareer.infrastructure.configuration.security.AppSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EmailVerificationListenerTest {

    @Mock
    JavaMailSender mailSender;
    @Mock
    VerificationService verificationService;
    @Mock
    AppSecurityConfiguration applicationConfiguration;

    @InjectMocks
    EmailVerificationListener listener;

    @Test
    void test_send_email() {
        String userName = "user";
        String email = "mail@mail.com";
        String id = UUID.randomUUID().toString();
        String host = "http://localhost:8080";
        String verificationUri = host.concat("/registration/verify/email").concat("?").concat("id=").concat(id).concat("&").concat("email=").concat(email);
        String emailText = String.format("Account activation link: %s", verificationUri);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("New account created");
        message.setText(emailText);
        message.setTo(email);

        Verification verification = new Verification(email);
        verification.setId(id);
        when(verificationService.createVerification(email)).thenReturn(verification);
        when(applicationConfiguration.getVerificationHost()).thenReturn(host);
        doNothing().when(mailSender).send(message);

        listener.onApplicationEvent(new UserRegistrationEvent(UserDto.builder().userName(userName).email(email).build()));

        verify(verificationService).createVerification(email);
        verify(mailSender).send(message);
    }
}