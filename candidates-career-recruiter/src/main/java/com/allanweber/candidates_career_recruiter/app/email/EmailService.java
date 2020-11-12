package com.allanweber.candidates_career_recruiter.app.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {
    public static final String MESSAGE_BODY = "$MESSAGE_BODY";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @SuppressWarnings("PMD")
    public void sendHtmlTemplate(String subject, String htmlBody, String... receipts) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
        try {

            String emailTemplate = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream("/mail/email.html"), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining());

            String emailMessage = emailTemplate.replace(MESSAGE_BODY, htmlBody);

            message.setSubject(subject);
            message.setText(emailMessage, true);
            message.setFrom(sender);
            message.setTo(receipts);
            mailSender.send(mimeMessage);
        } catch (Exception ex){
            log.error("Error sending application email", ex);
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Error sending activation email: %s", Optional.ofNullable(ex.getCause()).orElse(ex).getMessage()));
        }
    }
}
