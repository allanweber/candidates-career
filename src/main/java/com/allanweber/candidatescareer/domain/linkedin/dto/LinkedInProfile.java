package com.allanweber.candidatescareer.domain.linkedin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkedInProfile {

    private String firstName;

    private String lastName;

    private String profilePicture;
}