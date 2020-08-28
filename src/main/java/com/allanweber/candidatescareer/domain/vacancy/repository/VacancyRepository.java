package com.allanweber.candidatescareer.domain.vacancy.repository;

import com.allanweber.candidatescareer.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class VacancyRepository {
    private final UserService userService;
    private final VacancyMongoRepository mongoRepository;

    public List<Vacancy> findAll() {
        return mongoRepository.findAllByOwner(userService.getUserName());
    }

    public Optional<Vacancy> findById(String id) {
        return mongoRepository.findByIdAndOwner(id, userService.getUserName());
    }

    public Vacancy save(Vacancy vacancy) {
        Vacancy entity = vacancy.withOwner(userService.getUserName());
        return mongoRepository.save(entity);
    }


    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}
