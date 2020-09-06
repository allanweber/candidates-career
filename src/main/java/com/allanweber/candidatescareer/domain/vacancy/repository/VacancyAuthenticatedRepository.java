package com.allanweber.candidatescareer.domain.vacancy.repository;

import com.allanweber.candidatescareer.domain.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class VacancyAuthenticatedRepository {
    private final AuthService authService;
    private final VacancyMongoRepository mongoRepository;

    public List<Vacancy> findAll() {
        return mongoRepository.findAllByOwner(authService.getUserName());
    }

    public Optional<Vacancy> findById(String id) {
        return mongoRepository.findByIdAndOwner(id, authService.getUserName());
    }

    public Vacancy save(Vacancy vacancy) {
        Vacancy entity = vacancy.withOwner(authService.getUserName());
        return mongoRepository.save(entity);
    }

    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}
