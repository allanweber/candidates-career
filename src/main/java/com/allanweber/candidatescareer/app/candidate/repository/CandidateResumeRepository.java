package com.allanweber.candidatescareer.app.candidate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CandidateResumeRepository extends MongoRepository<CandidateResume, String> {
    Optional<CandidateResume> findByCandidateIdAndOwner(String id, String owner);
}
