package com.allanweber.candidatescareer.infrastructure.configuration.registration;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

class PasswordConfigurationTest {

    PasswordConfiguration passwordConfiguration = new PasswordConfiguration();

    @Test
    void passwordEncoder() {
        PasswordEncoder passwordEncoder = passwordConfiguration.passwordEncoder();
        assertThat(passwordEncoder, instanceOf(BCryptPasswordEncoder.class));
    }
}