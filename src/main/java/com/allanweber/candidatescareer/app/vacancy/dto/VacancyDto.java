package com.allanweber.candidatescareer.app.vacancy.dto;

import com.allanweber.candidatescareer.app.shared.Skill;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class  VacancyDto {
    private final String id;

    @NotBlank(message = "Name é obrigatório")
    private final String name;

    @NotBlank(message = "Descrição é obrigatório")
    private final String description;

    @NotEmpty(message = "Skills é obrigatório")
    private final List<Skill> skills;

    private final String location;

    @Builder.Default
    private final boolean remote = false;
}

