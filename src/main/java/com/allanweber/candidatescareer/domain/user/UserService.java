package com.allanweber.candidatescareer.domain.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String getUserName() {
        return "anonymous";
    }
}
