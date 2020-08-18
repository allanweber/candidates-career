package com.allanweber.candidatescareer.domain.candidate.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "candidate")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Candidate {
    @Id
    private String id;

    private String name;

    private String gitHubProfile;

    private String linkedInProfile;
}
