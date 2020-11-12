package com.allanweber.candidates_career_recruiter.authentication.user.registration.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "email_verification")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Verification {
    private String id;

    private String email;

    public Verification(String email) {
        this.email = email;
    }
}
