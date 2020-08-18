package com.allanweber.candidatescareer.domain.candidate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CandidateRepository extends MongoRepository<Candidate, String> {
}
