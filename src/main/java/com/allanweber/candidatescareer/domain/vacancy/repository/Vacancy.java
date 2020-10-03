package com.allanweber.candidatescareer.domain.vacancy.repository;

import com.allanweber.candidatescareer.domain.vacancy.dto.Skill;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Document(collection = "vacancy")
@RequiredArgsConstructor
@Getter
@With
@Builder
@EqualsAndHashCode
public class Vacancy {
    @Id
    private final String id;

    @NotBlank
    private final String owner;

    private final String name;

    private final String description;

    private final List<Skill> skills;
}
