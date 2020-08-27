package com.allanweber.candidatescareer.infrastructure.handler;

import com.allanweber.candidatescareer.infrastructure.handler.dto.ResponseErrorDto;
import com.allanweber.candidatescareer.infrastructure.handler.dto.ViolationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    private static final String CLIENT_EXCEPTION_HAPPENED = "Client Exception happened";
    private static final String UNEXPECTED_EXCEPTION_HAPPENED = "Unexpected Exception happened";
    private static final String CONSTRAINT_MESSAGE = "Constraints violations found.";
    private static final Integer SIZE = 1;

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ResponseErrorDto> handleClientException(HttpClientErrorException ex) {
        log.error(CLIENT_EXCEPTION_HAPPENED, ex);
        String message = Objects.requireNonNull(ex.getMessage()).replace(Integer.toString(ex.getRawStatusCode()), "").trim();
        return ResponseEntity.status(ex.getStatusCode()).body(new ResponseErrorDto(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorDto> handleException(Exception ex) {
        log.error(UNEXPECTED_EXCEPTION_HAPPENED, ex);
        String message = Optional.ofNullable(ex.getCause()).orElse(ex).getMessage();
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ResponseErrorDto(message));
    }

    //TODO: need test
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(CONSTRAINT_MESSAGE, e);
        List<ViolationDto> errors = new ArrayList<>();

        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            String fieldName = error.getObjectName();
            if (error instanceof FieldError) {
                fieldName = ((FieldError) error).getField();
            }
            errors.add(ViolationDto.create(fieldName, error.getDefaultMessage()));
        }

        ResponseErrorDto response = new ResponseErrorDto(errors.get(0).getMessage(), errors);
        if (errors.size() > SIZE) {
            response.setMessage(CONSTRAINT_MESSAGE);
        }

        return response;
    }
}