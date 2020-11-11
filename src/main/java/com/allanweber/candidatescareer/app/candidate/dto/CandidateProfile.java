package com.allanweber.candidatescareer.app.candidate.dto;

import com.allanweber.candidatescareer.app.shared.CandidateExperience;
import com.allanweber.candidatescareer.core.utilities.Trimmable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CandidateProfile implements Trimmable {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 128, min = 5, message = "Nome é muito grande ou muito pequeno")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email é inválido")
    @Size(max = 128, message = "Email é muito grande")
    private String email;

    @NotBlank(message = "Localização é obrigatória")
    @Size(max = 128, message = "Localização é muito grande")
    private String location;

    @NotBlank(message = "Biografia é obrigatória")
    @Size(max = 10_000, message = "Biografia é muito grande")
    private String bio;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20, message = "Telefone é muito grande")
    private String phone;

    private List<CandidateExperience> experiences;

    private LocalDateTime lastUpdate;
}
