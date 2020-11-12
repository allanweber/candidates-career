package com.allanweber.candidates_career_recruiter.app.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CandidateExperience {

    @NotBlank(message = "Empresa é obrigatório")
    @Size(max = 128, min = 5, message = "Empresa é muito grande ou muito pequeno")
    private String companyName;

    @NotBlank(message = "Localização da empresa é obrigatório")
    @Size(max = 128, min = 5, message = "Localização da empresa é muito grande ou muito pequeno")
    private String companyLocation;

    @NotBlank(message = "Posição é obrigatório")
    @Size(max = 128, min = 5, message = "Posição é muito grande ou muito pequeno")
    private String position;

    @NotNull
    private LocalDate start;

    private LocalDate end;

    @NotBlank(message = "Descrição é obrigatório")
    @Size(max = 10_000, min = 5, message = "Descrição é muito grande ou muito pequeno")
    private String description;

    @NotEmpty
    private List<Skill> skills;
}

