package com.allanweber.candidates_career_recruiter.authentication.user.registration.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document(collection = "change-password")
@AllArgsConstructor
@Data
@Builder
public class ChangePassword {
    @Id
    private String id;

    @NotBlank
    private String email;

    @NotBlank
    private String hash;
}
