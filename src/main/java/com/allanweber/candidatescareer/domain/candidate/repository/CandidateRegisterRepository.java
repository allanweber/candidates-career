package com.allanweber.candidatescareer.domain.candidate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CandidateRegisterRepository extends MongoRepository<CandidateRegister, String> {
    Optional<CandidateRegister> findByCandidateIdAndVacancyId(String candidateId, String vacancyId);
}
