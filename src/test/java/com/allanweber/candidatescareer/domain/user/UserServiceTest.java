package com.allanweber.candidatescareer.domain.user;

import com.allanweber.candidatescareer.domain.user.dto.UserDto;
import com.allanweber.candidatescareer.domain.user.registration.dto.UserRegistration;
import com.allanweber.candidatescareer.domain.user.repository.AppUser;
import com.allanweber.candidatescareer.domain.user.repository.AppUserRepository;
import com.allanweber.candidatescareer.infrastructure.configuration.security.AppSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    private static final String USER_NAME = "userName";

    @Mock
    AppUserRepository repository;

    @Mock
    AppSecurityConfiguration appSecurityConfiguration;

    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    UserService service;

    @Test
    void test_loadUserByUsername() {
        AppUser user = new AppUser("user", "password", "user", "user", "email", true, true,
                Collections.singletonList(new Authority(AuthoritiesHelper.ADMIN)));
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername(USER_NAME);
        assertEquals(user.getUserName(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(user.getEnabled(), userDetails.isEnabled());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(userDetails.getAuthorities());
        assertEquals(2, grantedAuthorities.size());
        assertTrue(grantedAuthorities.contains(new SimpleGrantedAuthority(AuthoritiesHelper.ROLE_USER)));
        assertTrue(grantedAuthorities.contains(new SimpleGrantedAuthority(AuthoritiesHelper.ROLE_ADMIN)));
    }

    @Test
    void test_loadUserByUsername_UsernameNotFoundException() {
        when(repository.findById(USER_NAME)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(USER_NAME));
    }

    @Test
    void test_loadUserByUsername_authorities_null_exception() {
        AppUser user = new AppUser("user", "password", "user", "user", "email", true, true, null);
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(user));
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(USER_NAME));
    }

    @Test
    void test_getByUserName() {
        AppUser user = new AppUser("user", "password", "user", "user", "email", true, true,
                Collections.singletonList(new Authority(AuthoritiesHelper.ADMIN)));
        when(repository.findById(USER_NAME)).thenReturn(Optional.of(user));

        UserDto userDto = service.getByUserName(USER_NAME);
        assertEquals(user.getUserName(), userDto.getUserName());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEnabled(), userDto.getEnabled());
        assertEquals(user.getVerified(), userDto.getVerified());
        assertEquals(1, userDto.getAuthorities().size());
        assertEquals("ADMIN", userDto.getAuthorities().get(0).getAuthority());
    }

    @Test
    void test_getByUserName_HttpClientErrorException() {
        when(repository.findById(USER_NAME)).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.getByUserName(USER_NAME), "User not found");
    }

    @Test
    void getAll() {
        AppUser user = new AppUser("user", "password", "user", "user", "email", true, true,
                Collections.singletonList(new Authority(AuthoritiesHelper.ADMIN)));
        when(repository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDto> all = service.getAll();
        assertEquals(user.getUserName(), all.get(0).getUserName());
        assertEquals(user.getEmail(), all.get(0).getEmail());
        assertEquals(user.getFirstName(), all.get(0).getFirstName());
        assertEquals(user.getLastName(), all.get(0).getLastName());
        assertEquals(user.getEnabled(), all.get(0).getEnabled());
        assertEquals(user.getVerified(), all.get(0).getVerified());
        assertEquals(1, all.get(0).getAuthorities().size());
        assertEquals("ADMIN", all.get(0).getAuthorities().get(0).getAuthority());
    }

    @Test
    void test_setUserVerified_HttpClientErrorException() {
        when(repository.findById(USER_NAME)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.setUserVerified(USER_NAME));
    }

    @Test
    void test_createUser() {
        AppUser user = new AppUser(USER_NAME, "password", "user", "user", "email", true, true,
                Collections.singletonList(new Authority(AuthoritiesHelper.USER)));

        UserRegistration registration = new UserRegistration("user", "user", USER_NAME, "email", "pass", "pass");
        when(repository.existsByUserName(USER_NAME)).thenReturn(false);
        when(repository.existsByEmail(any())).thenReturn(false);
        when(appSecurityConfiguration.isEmailVerificationEnabled()).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("pass");
        when(repository.save(any())).thenReturn(user);

        UserDto userDto = service.createUser(registration);
        assertEquals(registration.getUserName(), userDto.getUserName());
        assertEquals(registration.getEmail(), userDto.getEmail());
        assertEquals(registration.getFirstName(), userDto.getFirstName());
        assertEquals(registration.getLastName(), userDto.getLastName());
        assertTrue(userDto.getEnabled());
        assertTrue(userDto.getVerified());
        assertEquals(1, userDto.getAuthorities().size());
        assertEquals("USER", userDto.getAuthorities().get(0).getAuthority());
    }

    @Test
    void test_createUser_userNameExists_HttpClientErrorException() {
        UserRegistration registration = new UserRegistration("user", "user", USER_NAME, "email", "pass", "pass");
        when(repository.existsByUserName(USER_NAME)).thenReturn(true);
        assertThrows(HttpClientErrorException.class, () -> service.createUser(registration), String.format("User name %s already exists", USER_NAME));
    }

    @Test
    void test_createUser_emailExists_HttpClientErrorException() {
        UserRegistration registration = new UserRegistration("user", "user", USER_NAME, "email", "pass", "pass");
        when(repository.existsByUserName(USER_NAME)).thenReturn(false);
        when(repository.existsByEmail(any())).thenReturn(true);
        assertThrows(HttpClientErrorException.class, () -> service.createUser(registration), "Email email already exists");
    }
}
