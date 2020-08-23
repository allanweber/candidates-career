package com.allanweber.candidatescareer.domain.vacancy.repository;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "vacancy")
@RequiredArgsConstructor
@Getter
@With
@Builder
public class Vacancy {
    @Id
    private final String id;

    private final String name;

    private final List<String> skills;
}
