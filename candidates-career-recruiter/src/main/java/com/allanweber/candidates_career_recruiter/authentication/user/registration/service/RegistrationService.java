package com.allanweber.candidates_career_recruiter.authentication.user.registration.service;

import com.allanweber.candidates_career_recruiter.app.email.EmailService;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.dto.ChangePasswordRequest;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.dto.RememberMeRequest;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.dto.RememberMeResponse;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.repository.ChangePassword;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.repository.ChangePasswordRepository;
import com.allanweber.candidates_career_recruiter.authentication.user.repository.AppUser;
import com.allanweber.candidates_career_recruiter.authentication.user.repository.AppUserRepository;
import com.allanweber.candidates_career_recruiter.authentication.user.service.UserService;
import com.allanweber.candidates_career_recruiter.infrastructure.configuration.AppHostConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@SuppressWarnings("PMD")
public class RegistrationService {

    private static final String CHANGE_PASSWORD_PATH = "auth/change-password";
    public static final String NAME = "$NAME";
    public static final String URL = "$URL";
    public static final String SUBJECT = "Candidates Career - alterar senha.";

    private final AppUserRepository appUserRepository;
    private final ChangePasswordRepository changePasswordRepository;
    private final AppHostConfiguration appHostConfiguration;
    private final EmailService emailService;
    private final UserService userService;

    public RememberMeResponse rememberMe(RememberMeRequest rememberMe) {
        Optional<AppUser> user = getAppUser(rememberMe.getEmail());
        if (user.isEmpty()) {
            return new RememberMeResponse("Usuário não encontrado");
        }

        if (!user.get().getEnabled() || !user.get().getVerified()) {
            return new RememberMeResponse("Você ainda não confirmou seu email.");
        }

        changePasswordRepository.getByEmail(rememberMe.getEmail())
                .ifPresent(changePasswordRepository::delete);
        String hash = UUID.randomUUID().toString().concat(UUID.randomUUID().toString());
        changePasswordRepository.insert(ChangePassword.builder().email(rememberMe.getEmail()).hash(hash).build());

        String changePasswordUrl = UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(CHANGE_PASSWORD_PATH)
                .path(hash)
                .build()
                .toUriString();

        String name = user.get().getFirstName();
        String emailTemplate = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/mail/change-password.html"), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());

        String emailMessage = emailTemplate.replace(NAME, name).replace(URL, changePasswordUrl);
        emailService.sendHtmlTemplate(SUBJECT, emailMessage, user.get().getEmail());

        return new RememberMeResponse();
    }

    public RememberMeResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        if(!changePasswordRequest.getPassword().equals(changePasswordRequest.getConfirmPassword())) {
            return new RememberMeResponse("Senha e confirmação de senha devem ser exatamente iguais.");
        }

        Optional<AppUser> user = getAppUser(changePasswordRequest.getEmail());
        if (user.isEmpty()) {
            return new RememberMeResponse("Usuário não encontrado");
        }

        if (!user.get().getEnabled() || !user.get().getVerified()) {
            return new RememberMeResponse("Você ainda não confirmou seu email.");
        }

        Optional<ChangePassword> changePassword = changePasswordRepository.getByEmailAndHash(changePasswordRequest.getEmail(), changePasswordRequest.getHash());
        if(changePassword.isEmpty()) {
            return new RememberMeResponse("Alteração de senha é inválida.");
        }

        changePasswordRepository.delete(changePassword.get());
        userService.changePassword(changePasswordRequest.getEmail(), changePasswordRequest.getPassword());

        return new RememberMeResponse();
    }

    private Optional<AppUser> getAppUser(String email) {
        return appUserRepository.findByEmail(email);
    }
}
