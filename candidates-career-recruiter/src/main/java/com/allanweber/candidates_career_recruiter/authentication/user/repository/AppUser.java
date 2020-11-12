package com.allanweber.candidates_career_recruiter.authentication.user.repository;

import com.allanweber.candidates_career_recruiter.authentication.user.dto.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@AllArgsConstructor
@Data
@Builder
public class AppUser {
    @Id
    private String userName;

    private String password;

    private String firstName;

    private String lastName;

    private String email;

    @Builder.Default
    private Boolean enabled = false;

    @Builder.Default
    private Boolean verified = false;

    private List<Authority> authorities;

    public void addAuthority(String authority) {
        if (authorities == null) {
            authorities = new ArrayList<>();
        }
        authorities.add(new Authority(authority));
    }
}
