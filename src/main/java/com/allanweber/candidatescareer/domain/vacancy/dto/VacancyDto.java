package com.allanweber.candidatescareer.domain.vacancy.dto;

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
public class VacancyDto {
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotEmpty(message = "Skills are required")
    private List<Skill> skills;
}
