package com.allanweber.candidatescareer.domain.vacancy.repository;

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

    private final List<String> skills;
}
