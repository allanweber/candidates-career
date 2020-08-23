package com.allanweber.candidatescareer.domain.linkedin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class LinkedInData {

    private LinkedInName firstName;

    private LinkedInName lastName;

    private ProfilePicture profilePicture;
}