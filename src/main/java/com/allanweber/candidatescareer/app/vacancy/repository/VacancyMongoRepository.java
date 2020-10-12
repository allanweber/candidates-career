package com.allanweber.candidatescareer.app.vacancy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VacancyMongoRepository extends MongoRepository<Vacancy, String> {
    List<Vacancy> findAllByOwner(String owner);

    Optional<Vacancy> findByIdAndOwner(String id, String owner);
}
