package com.allanweber.candidatescareer.domain.candidate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CandidateRepository extends MongoRepository<Candidate, String> {

    Optional<Candidate> getByEmail(String email);
}
