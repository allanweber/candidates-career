package com.allanweber.candidatescareer.domain.user.dto;

import com.allanweber.candidatescareer.domain.user.Authority;
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
        String fullName = this.firstName;
        if(Objects.nonNull(this.lastName)){
            fullName = fullName + " " + this.lastName;
        }
        return fullName;
    }
}