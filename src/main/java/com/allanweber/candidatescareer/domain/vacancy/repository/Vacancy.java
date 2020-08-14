package com.allanweber.candidatescareer.domain.vacancy.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "vacancy")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Vacancy {

    @Id
    private String id;

    private String name;

    private List<String> skills;
}
