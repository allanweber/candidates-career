package com.allanweber.candidatescareer.domain.candidate.repository;

import com.allanweber.candidatescareer.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CandidateRepository  {

    private final UserService userService;
    private final CandidateMongoRepository mongoRepository;

    public List<Candidate> findAll() {
        return mongoRepository.findAllByOwner(userService.getUserName());
    }

    public Optional<Candidate> findById(String id) {
        return mongoRepository.findByIdAndOwner(id, userService.getUserName());
    }

    public Candidate save(Candidate candidate) {
        Candidate entity = candidate.withOwner(userService.getUserName());
        return mongoRepository.save(entity);
    }

    public Optional<Candidate> getByEmail(String email) {
        return mongoRepository.getByEmailAndOwner(email, userService.getUserName());
    }

    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}
