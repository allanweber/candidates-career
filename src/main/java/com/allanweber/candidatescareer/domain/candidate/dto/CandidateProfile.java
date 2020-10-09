package com.allanweber.candidatescareer.domain.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CandidateProfile {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email é inválido")
    private String email;

    @NotBlank(message = "Localização é obrigatória")
    private String location;

    @NotBlank(message = "Biografia é obrigatória")
    private String bio;

    @NotBlank(message = "Telefone é obrigatório")
    private String phone;

    private List<CandidateExperience> experiences;

    private LocalDateTime lastUpdate;
}
