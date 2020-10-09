package com.allanweber.candidatescareer.domain.candidate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateMongoRepository extends MongoRepository<Candidate, String> {
    Optional<Candidate> getByEmailAndOwner(String email, String owner);

    List<Candidate> getAllByEmailAndOwner(String email, String owner);

    List<Candidate> findAllByOwner(String owner);

    Optional<Candidate> findByIdAndOwner(String id, String owner);
}
