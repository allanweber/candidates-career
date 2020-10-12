package com.allanweber.candidatescareer.authentication.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {

    private String userName;

    private String firstName;

    private String lastName;

    private String email;

    @Builder.Default
    private Boolean enabled = false;

    @Builder.Default
    private Boolean verified = false;

    private List<Authority> authorities;

    public String getFullName() {
        StringBuilder fullName = new StringBuilder(this.firstName);
        if(Objects.nonNull(this.lastName)){
            fullName.append(' ').append(this.lastName);
        }
        return fullName.toString();
    }
}