package com.allanweber.candidatescareer.domain.user;

import com.allanweber.candidatescareer.domain.user.repository.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsHelperTest {

    @Test
    void loadCommonUser() {
        AppUser user = new AppUser("user", "password", "user", "user", "email", true, true, null);
        UserDetails userDetails = UserDetailsHelper.createUserDetails(user);
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals(AuthoritiesHelper.ROLE_USER, userDetails.getAuthorities().iterator().next().getAuthority());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadAdminUser() {
        AppUser user = new AppUser("user", "password", "user", "user", "email", true, true,
                Collections.singletonList(new Authority(AuthoritiesHelper.ADMIN)));
        UserDetails userDetails = UserDetailsHelper.createUserDetails(user);
        assertEquals(2, userDetails.getAuthorities().size());

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(userDetails.getAuthorities());

        assertEquals(2, grantedAuthorities.size());
        assertTrue(grantedAuthorities.contains(new SimpleGrantedAuthority(AuthoritiesHelper.ROLE_USER)));
        assertTrue(grantedAuthorities.contains(new SimpleGrantedAuthority(AuthoritiesHelper.ROLE_ADMIN)));
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void user_verifiedFalse_is_enabled_false() {
        AppUser user = new AppUser("user", "password", "user", "user", "email", true, false, null);
        UserDetails userDetails = UserDetailsHelper.createUserDetails(user);
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void user_enabledFalse_is_enabled_false() {
        AppUser user = new AppUser("user", "password", "user", "user", "email", false, true, null);
        UserDetails userDetails = UserDetailsHelper.createUserDetails(user);
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void user_enabledFalse_and_verifiedFalse_is_enabled_false() {
        AppUser user = new AppUser("user", "password", "user", "user", "email", false, false, null);
        UserDetails userDetails = UserDetailsHelper.createUserDetails(user);
        assertFalse(userDetails.isEnabled());
    }
}