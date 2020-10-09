package com.allanweber.candidatescareer.domain.candidate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class CandidateUpdate {
    private String id;

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email é inválido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String phone;

    private String location;

    private String bio;

    private String currentCompany;
}
