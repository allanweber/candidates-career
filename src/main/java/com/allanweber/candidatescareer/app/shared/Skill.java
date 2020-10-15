package com.allanweber.candidatescareer.app.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class Skill {

    @NotBlank(message = "Nome da habilidade é obrigatório")
    private String name;

    @NotBlank(message = "Anos de experiência na habilidade é obrigatório")
    private int years;
}

