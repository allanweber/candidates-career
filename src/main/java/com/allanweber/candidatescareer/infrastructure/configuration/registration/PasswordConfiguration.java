package com.allanweber.candidatescareer.infrastructure.configuration.registration;

import org.passay.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PasswordConfiguration {
    private static final int MIN_COMPLEX_RULES = 3;
    private static final int MIN_UPPER_CASE_CHARS = 1;
    private static final int MIN_LOWER_CASE_CHARS = 1;
    private static final int MIN_DIGIT_CASE_CHARS = 1;
    private static final int MIN_SPECIAL_CASE_CHARS = 1;
    private static final int MAX_REPETITIVE_CHARS = 3;

    @Bean
    public PasswordValidator getPasswordValidator() {
        List<Rule> passwordRules = new ArrayList<>();
        passwordRules.add(new LengthRule(10, 128));
        CharacterCharacteristicsRule passwordChars =
                new CharacterCharacteristicsRule(MIN_COMPLEX_RULES,
                        new CharacterRule(EnglishCharacterData.UpperCase, MIN_UPPER_CASE_CHARS),
                        new CharacterRule(EnglishCharacterData.LowerCase, MIN_LOWER_CASE_CHARS),
                        new CharacterRule(EnglishCharacterData.Digit, MIN_DIGIT_CASE_CHARS),
                        new CharacterRule(EnglishCharacterData.Special, MIN_SPECIAL_CASE_CHARS));
        passwordRules.add(passwordChars);
        passwordRules.add(new RepeatCharacterRegexRule(MAX_REPETITIVE_CHARS));
        return new PasswordValidator(passwordRules);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
