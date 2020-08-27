package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.SocialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AppsAuthorizationControllerTest {

    @Mock
    SocialService socialService;

    @InjectMocks
    AppsAuthorizationController controller;

    @Test
    void socialAuthorizationLinkedIn() {

        when(socialService.getAuthorizationUri(anyString(), any())).thenReturn("http://linkedin.com");
        ResponseEntity<Void> response = controller.socialAuthorizationLinkedIn("");
        assertEquals(302, response.getStatusCodeValue());
        assertEquals("http://linkedin.com", Objects.requireNonNull(response.getHeaders().get("Location")).get(0));
    }

    @Test
    void linkedInCallback() {
        doNothing().when(socialService).callBackLinkedIn(anyString(), anyString());
        ResponseEntity<Void> response = controller.linkedInCallback("", "");
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}