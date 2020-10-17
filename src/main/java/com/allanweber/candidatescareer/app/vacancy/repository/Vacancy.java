package com.allanweber.candidatescareer.app.vacancy.repository;

import com.allanweber.candidatescareer.app.shared.Skill;
import com.allanweber.candidatescareer.app.vacancy.dto.Salary;
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

    private final String location;

    @Builder.Default
    private final boolean remote = false;

    private final Salary salary;
}
