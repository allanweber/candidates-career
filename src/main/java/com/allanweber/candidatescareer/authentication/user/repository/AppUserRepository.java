package com.allanweber.candidatescareer.authentication.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Boolean existsByUserName(String userName);

    Boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);
}
