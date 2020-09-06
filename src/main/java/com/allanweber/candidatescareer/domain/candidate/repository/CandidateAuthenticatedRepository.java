package com.allanweber.candidatescareer.domain.candidate.repository;

import com.allanweber.candidatescareer.domain.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CandidateAuthenticatedRepository {

    private final AuthService authService;
    private final CandidateMongoRepository mongoRepository;

    public List<Candidate> findAll() {
        return mongoRepository.findAllByOwner(authService.getUserName());
    }

    public Optional<Candidate> findById(String id) {
        return mongoRepository.findByIdAndOwner(id, authService.getUserName());
    }

    public Candidate save(Candidate candidate) {
        Candidate entity = candidate.withOwner(authService.getUserName());
        return mongoRepository.save(entity);
    }

    public Optional<Candidate> getByEmail(String email) {
        return mongoRepository.getByEmailAndOwner(email, authService.getUserName());
    }

    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}
