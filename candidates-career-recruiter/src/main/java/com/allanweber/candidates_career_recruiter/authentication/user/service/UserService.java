package com.allanweber.candidates_career_recruiter.authentication.user.service;

import com.allanweber.candidates_career_recruiter.authentication.user.dto.AuthoritiesHelper;
import com.allanweber.candidates_career_recruiter.authentication.user.dto.UserDto;
import com.allanweber.candidates_career_recruiter.authentication.user.mapper.UserMapper;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.dto.UserRegistration;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.event.UserRegistrationEvent;
import com.allanweber.candidates_career_recruiter.authentication.user.repository.AppUser;
import com.allanweber.candidates_career_recruiter.authentication.user.repository.AppUserRepository;
import com.allanweber.candidates_career_recruiter.infrastructure.configuration.security.AppSecurityConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;
    private final AppSecurityConfiguration appSecurityConfiguration;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public UserDetails loadUserByUsername(String email) {
        AppUser user = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        if (user.getAuthorities() == null || user.getAuthorities().isEmpty()) {
            throw new UsernameNotFoundException("No authorities for the user");
        }
        return UserDetailsHelper.createUserDetails(user);
    }

    public List<UserDto> getAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public UserDto getByUserName(String userName) {
        return repository.findById(userName)
                .map(UserMapper::mapToDto)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, "User not found"));
    }

    public UserDto createUser(UserRegistration userRegistration) {

        if (userNameExists(userRegistration.getUserName())) {
            throw new HttpClientErrorException(BAD_REQUEST, String.format("User name %s already exists", userRegistration.getUserName()));
        }

        if (emailExists(userRegistration.getEmail())) {
            throw new HttpClientErrorException(BAD_REQUEST, String.format("Email %s already exists", userRegistration.getEmail()));
        }
        AppUser userToSave = UserMapper.toEntity(userRegistration);
        userToSave.setPassword(encoder.encode(userToSave.getPassword()));
        userToSave.setEnabled(!appSecurityConfiguration.isEmailVerificationEnabled());
        userToSave.setVerified(!appSecurityConfiguration.isEmailVerificationEnabled());
        userToSave.addAuthority(AuthoritiesHelper.USER);
        AppUser userSaved = repository.save(userToSave);
        UserDto userDto = UserMapper.mapToDto(userSaved);
        if (appSecurityConfiguration.isEmailVerificationEnabled()) {
            eventPublisher.publishEvent(new UserRegistrationEvent(userDto));
        }
        return userDto;
    }

    public void setUserVerified(String email) {
        AppUser user = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        user.setEnabled(true);
        user.setVerified(true);
        repository.save(user);
    }

    public Boolean userNameExists(String userName) {
        return repository.existsByUserName(userName);
    }

    public Boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    public void changePassword(String email, String plainPassword) {
        AppUser user = repository.findByEmail(email)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, "User not found"));

        user.setPassword(encoder.encode(plainPassword));
        repository.save(user);
    }
}
