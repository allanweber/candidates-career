package com.allanweber.candidates_career_recruiter.app.applications.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DenyReason {

    @NonNull
    private ApplicationStatus option;

    @NotBlank(message = "Opção é obrigatório")
    private String optionText;

    @Size(max = 500, message = "Motivo especifico é muito grande")
    private String extraReason;
}
