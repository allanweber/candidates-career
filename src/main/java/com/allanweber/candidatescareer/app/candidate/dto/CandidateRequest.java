package com.allanweber.candidatescareer.app.candidate.dto;

import com.allanweber.candidatescareer.core.utilities.Trimmable;
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
public class CandidateRequest implements Trimmable {
    private String id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 128, min = 5, message = "Nome é muito grande ou muito pequeno")
    private String name;

    @NotBlank(message = "Informe o email")
    @Email(message = "Email é inválido")
    @Size(max = 128, message = "Email é muito grande")
    private String email;

    @Size(max = 20, message = "Telefone é muito grande")
    private String phone;
}
