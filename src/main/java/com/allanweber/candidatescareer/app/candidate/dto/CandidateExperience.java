package com.allanweber.candidatescareer.app.candidate.dto;

import com.allanweber.candidatescareer.app.vacancy.dto.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CandidateExperience {

    @NotBlank
    private String companyName;

    @NotBlank
    private String companyLocation;

    @NotBlank
    private String position;

    @NotNull
    private LocalDate start;

    private LocalDate end;

    @NotBlank
    private String description;

    @NotEmpty
    private List<Skill> skills;
}

