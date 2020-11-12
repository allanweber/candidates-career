package com.allanweber.candidates_career_recruiter.app.candidate.dto;

import com.allanweber.candidates_career_recruiter.core.utilities.Trimmable;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class CandidateUpdate implements Trimmable {
    @NotBlank(message = "Id é obrigatório")
    private String id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 128, min = 5, message = "Nome é muito grande ou muito pequeno")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email é inválido")
    @Size(max = 128, message = "Email é muito grande")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20, message = "Telefone é muito grande")
    private String phone;

    @Size(max = 128, message = "Localização é muito grande")
    private String location;

    @Size(max = 10_000, message = "Biografia é muito grande")
    private String bio;

    @Size(max = 128, message = "Localização é muito grande")
    private String currentCompany;
}
