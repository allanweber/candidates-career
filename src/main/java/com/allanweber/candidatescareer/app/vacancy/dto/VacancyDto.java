package com.allanweber.candidatescareer.app.vacancy.dto;

import com.allanweber.candidatescareer.app.shared.Skill;
import com.allanweber.candidatescareer.core.utilities.Trimmable;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class VacancyDto implements Trimmable {
    private final String id;

    @NotBlank(message = "Name é obrigatório")
    @Size(max = 128, min = 5, message = "Nome é muito grande ou muito pequeno")
    private final String name;

    @NotBlank(message = "Descrição é obrigatório")
    @Size(max = 10_000, min = 5, message = "Descrição é muito grande ou muito pequeno")
    private final String description;

    @NotEmpty(message = "Skills é obrigatório")
    private final List<Skill> skills;

    @Size(max = 128, message = "Localização é muito grande")
    private final String location;

    @Builder.Default
    private final boolean remote = false;

    private final Salary salary;
}

