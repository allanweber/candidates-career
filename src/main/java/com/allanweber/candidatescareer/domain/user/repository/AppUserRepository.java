package com.allanweber.candidatescareer.domain.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Boolean existsByUserName(String userName);

    Boolean existsByEmail(String email);
}
