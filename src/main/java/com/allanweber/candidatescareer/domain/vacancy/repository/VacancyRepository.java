package com.allanweber.candidatescareer.domain.vacancy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VacancyRepository extends MongoRepository<Vacancy, String> {
}
