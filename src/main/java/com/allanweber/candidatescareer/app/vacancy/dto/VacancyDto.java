package com.allanweber.candidatescareer.app.vacancy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class  VacancyDto {
    private String id;

    @NotBlank(message = "Name é obrigatório")
    private String name;

    @NotBlank(message = "Descrição é obrigatório")
    private String description;

    @NotEmpty(message = "Skills é obrigatório")
    private List<Skill> skills;
}