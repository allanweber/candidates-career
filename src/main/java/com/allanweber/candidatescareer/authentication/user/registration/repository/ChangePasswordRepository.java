package com.allanweber.candidatescareer.authentication.user.registration.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChangePasswordRepository extends MongoRepository<ChangePassword, String> {
    Optional<ChangePassword> getByEmailAndHash(String email, String hash);

    Optional<ChangePassword> getByEmail(String email);
}
