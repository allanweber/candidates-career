package com.allanweber.candidatescareer.authentication.user.registration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RememberMeResponse {
    private final boolean ok;
    private String error;

    public RememberMeResponse() {
        this.ok = true;
    }

    public RememberMeResponse(String error) {
        this.ok = false;
        this.error = error;
    }
}
