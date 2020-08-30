package com.allanweber.candidatescareer.domain.user;

import com.allanweber.candidatescareer.domain.user.dto.UserDto;
import com.allanweber.candidatescareer.domain.user.mapper.UserMapper;
import com.allanweber.candidatescareer.domain.user.registration.dto.UserRegistration;
import com.allanweber.candidatescareer.domain.user.repository.AppUser;
import com.allanweber.candidatescareer.domain.user.repository.AppUserRepository;
import com.allanweber.candidatescareer.infrastructure.configuration.registration.RegistrationConfiguration;
import lombok.RequiredArgsConstructor;
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
    private final RegistrationConfiguration registrationConfiguration;

    @Override
    public UserDetails loadUserByUsername(String userName) {
        AppUser user = repository.findById(userName).orElseThrow(() -> new UsernameNotFoundException(userName));
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
        userToSave.setEnabled(!registrationConfiguration.isEmailVerificationEnabled());
        userToSave.setVerified(!registrationConfiguration.isEmailVerificationEnabled());
        userToSave.addAuthority(AuthoritiesHelper.USER);
        AppUser userSaved = repository.save(userToSave);
        return UserMapper.mapToDto(userSaved);
//        if (registrationConfiguration.isEmailVerificationEnabled()) {
//            eventPublisher.publishEvent(new UserRegistrationEvent(userDto));
//        }
    }

    public void setUserVerified(String userName) {
        AppUser user = repository.findById(userName).orElseThrow(() -> new UsernameNotFoundException(userName));
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

}
