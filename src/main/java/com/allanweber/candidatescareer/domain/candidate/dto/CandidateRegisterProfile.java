package com.allanweber.candidatescareer.domain.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CandidateRegisterProfile {

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @NotBlank
    private String bio;

    private List<CandidateExperience> experiences;
}
