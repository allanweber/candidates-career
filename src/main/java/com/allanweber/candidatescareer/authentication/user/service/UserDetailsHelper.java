package com.allanweber.candidatescareer.authentication.user.service;


import com.allanweber.candidatescareer.authentication.user.dto.AuthoritiesHelper;
import com.allanweber.candidatescareer.authentication.user.repository.AppUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDetailsHelper {

    public static UserDetails createUserDetails(AppUser user) {
        List<String> authorities = new ArrayList<>();
        authorities.add(AuthoritiesHelper.ROLE_USER);
        if( Optional.ofNullable(user.getAuthorities()).orElse(Collections.emptyList()).stream().anyMatch(auth -> auth.getAuthority().equals(AuthoritiesHelper.ADMIN))){
            authorities.add(AuthoritiesHelper.ROLE_ADMIN);
        }
        return User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .disabled(!(user.getVerified() && user.getEnabled()))
                .authorities(buildAuthorities(authorities))
                .build();
    }

    private static List<GrantedAuthority> buildAuthorities(List<String> authorities) {
        List<GrantedAuthority> authList = new ArrayList<>(1);
        for (String authority : authorities) {
            authList.add(new SimpleGrantedAuthority(authority));
        }
        return authList;
    }
}