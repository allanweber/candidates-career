package com.allanweber.candidates_career_recruiter.app.applications.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateApplicationRepository extends MongoRepository<CandidateApplication, String> {
    Optional<CandidateApplication> findByCandidateIdAndVacancyId(String candidateId, String vacancyId);

    List<CandidateApplication> findByCandidateId(String candidateId);

    List<CandidateApplication> findByVacancyId(String vacancyId);
}
