package com.allanweber.candidates_career_recruiter.authentication.user.mapper;

import com.allanweber.candidates_career_recruiter.authentication.user.dto.UserDto;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.dto.UserRegistration;
import com.allanweber.candidates_career_recruiter.authentication.user.repository.AppUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static AppUser toEntity(UserRegistration dto) {
        UserRegistration userDto = Optional.ofNullable(dto)
                .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Invalid user registration"));
        return AppUser
                .builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .userName(userDto.getUserName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build();
    }

    public static UserDto mapToDto(AppUser entity) {
        return UserDto.builder()
                .userName(entity.getUserName())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .enabled(entity.getEnabled())
                .verified(entity.getVerified())
                .authorities(entity.getAuthorities())
                .build();
    }
}
