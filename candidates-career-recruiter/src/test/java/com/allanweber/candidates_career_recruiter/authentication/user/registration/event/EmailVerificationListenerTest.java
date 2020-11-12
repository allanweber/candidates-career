package com.allanweber.candidates_career_recruiter.authentication.user.registration.event;

import com.allanweber.candidates_career_recruiter.app.email.EmailService;
import com.allanweber.candidates_career_recruiter.authentication.user.dto.UserDto;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.repository.Verification;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.service.VerificationService;
import com.allanweber.candidates_career_recruiter.infrastructure.configuration.AppHostConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EmailVerificationListenerTest {

    @Mock
    EmailService emailService;
    @Mock
    VerificationService verificationService;
    @Mock
    AppHostConfiguration appHostConfiguration;

    @InjectMocks
    EmailVerificationListener listener;

    @Test
    void test_send_email() {
        String userName = "user";
        String email = "mail@mail.com";
        String id = UUID.randomUUID().toString();
        String host = "http://localhost:8080";
        String verificationUri = host.concat("/registration/verify/email").concat("?").concat("id=").concat(id).concat("&").concat("email=").concat(email);
        String emailTemplate = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/mail/registration.html"), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());
        String emailMessage = emailTemplate.replace("$NAME", userName).replace("$URL", verificationUri);

        Verification verification = new Verification(email);
        verification.setId(id);
        when(verificationService.createVerification(email)).thenReturn(verification);
        when(appHostConfiguration.getBackEnd()).thenReturn(host);
        doNothing().when(emailService).sendHtmlTemplate("Candidates Career - verificar conta de email.", emailMessage, email);



        listener.onApplicationEvent(new UserRegistrationEvent(UserDto.builder().firstName(userName).userName(userName).email(email).build()));

        verify(verificationService).createVerification(email);
        verify(emailService).sendHtmlTemplate("Candidates Career - verificar conta de email.", emailMessage, email);
    }
}