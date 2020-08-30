package com.allanweber.candidatescareer.infrastructure.listeners;

import com.allanweber.candidatescareer.domain.user.AuthoritiesHelper;
import com.allanweber.candidatescareer.domain.user.Authority;
import com.allanweber.candidatescareer.domain.user.repository.AppUser;
import com.allanweber.candidatescareer.domain.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class UserStartListener implements ApplicationListener<ApplicationReadyEvent> {
    private final AppUserRepository userRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        insertUserAdmin();
    }

    private void insertUserAdmin() {
        if(userRepository.findById("admin").isPresent()){
            return;
        }
        List<Authority> authorities = Arrays.asList(new Authority(AuthoritiesHelper.ADMIN), new Authority(AuthoritiesHelper.USER));
        AppUser admin = new AppUser(
                "admin",
                "$2y$12$oUzCDll71b3BvxC0Upywl.49mZmRuXmNhZ1JyqRp.iD87Od3nlada",
                "Administrator",
                "",
                "a.cassianoweber@gmail.com",
                true,
                true,
                authorities
        );
        userRepository.insert(admin);
    }
}
