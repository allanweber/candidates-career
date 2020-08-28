package com.allanweber.candidatescareer.infrastructure.handler;

import com.allanweber.candidatescareer.infrastructure.handler.dto.ResponseErrorDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    ApiExceptionHandler apiExceptionHandler = new ApiExceptionHandler();

    @Test
    void handleClientException_no_message() {
        ResponseEntity<ResponseErrorDto> response = apiExceptionHandler.handleClientException(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("NOT_FOUND", Objects.requireNonNull(response.getBody()).getMessage() );
        assertNull(Objects.requireNonNull(response.getBody()).getDetail());
    }

    @Test
    void handleClientException_with_message() {
        ResponseEntity<ResponseErrorDto> response =
                apiExceptionHandler.handleClientException(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "any message"));
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("any message", Objects.requireNonNull(response.getBody()).getMessage() );
        assertNull(Objects.requireNonNull(response.getBody()).getDetail());
    }

    @Test
    void handleException() {
        ResponseEntity<ResponseErrorDto> response =
                apiExceptionHandler.handleException(new RuntimeException("any message"));
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("any message", Objects.requireNonNull(response.getBody()).getMessage() );
        assertNull(Objects.requireNonNull(response.getBody()).getDetail());
    }

    @Test
    void handleException_with_cause() {
        RuntimeException causeMessage = new RuntimeException("cause message");
        ResponseEntity<ResponseErrorDto> response =
                apiExceptionHandler.handleException(new RuntimeException("any message", causeMessage));
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("cause message", Objects.requireNonNull(response.getBody()).getMessage() );
        assertNull(Objects.requireNonNull(response.getBody()).getDetail());
    }
}