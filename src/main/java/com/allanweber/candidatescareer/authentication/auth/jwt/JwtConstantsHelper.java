package com.allanweber.candidatescareer.authentication.auth.jwt;

import java.util.concurrent.TimeUnit;

public final class JwtConstantsHelper {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "any-app-api";
    public static final String TOKEN_AUDIENCE = "any-app-app";
    public static final String AUTHORITIES = "authorities";
    public static final String HEADER_TYP = "typ";
    public static final long TOKEN_DURATION_SECONDS = TimeUnit.HOURS.toSeconds(1);

    private JwtConstantsHelper() {
        throw new IllegalStateException("Cannot create instance of static util class");
    }
}
