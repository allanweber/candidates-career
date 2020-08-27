package com.allanweber.candidatescareer.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserService service = new UserService();

    @Test
    void getUserName() {
        assertEquals("anonymous", service.getUserName());
    }
}