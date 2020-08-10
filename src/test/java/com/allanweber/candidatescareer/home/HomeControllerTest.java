package com.allanweber.candidatescareer.home;

import com.allanweber.candidatescareer.configuration.AppConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
class HomeControllerTest {

    @Mock
    private AppConfiguration appConfiguration;

    @InjectMocks
    private HomeController homeController;

    @Test
    void check_property() {
        when(appConfiguration.getSomeProperty()).thenReturn("message");

        ResponseEntity<?> response = homeController.get();

        assertEquals(OK, response.getStatusCode());
//        assertEquals("message", response.getBody());
    }
}