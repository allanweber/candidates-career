package com.allanweber.candidatescareer.domain.candidate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class CandidateDto {
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(regexp = "https?://.+", message = "Git hub url is invalid")
    private String gitHubProfile;
}
