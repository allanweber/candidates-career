package com.allanweber.candidates_career_recruiter.authentication.user.registration.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VerificationRepository extends MongoRepository<Verification, String> {
    boolean existsByEmail(String email);

    Optional<Verification> findByEmail(String email);

    Optional<Verification> findByIdAndEmail(String id, String email);
}
