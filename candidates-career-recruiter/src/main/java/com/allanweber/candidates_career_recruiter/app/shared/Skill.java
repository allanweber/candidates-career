package com.allanweber.candidates_career_recruiter.app.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class Skill {

    @NotBlank(message = "Nome da habilidade é obrigatório")
    @Size(max = 50, message = "Nome da habilidade é muito grande")
    private String name;

    @NotBlank(message = "Anos de experiência na habilidade é obrigatório")
    private int years;
}

