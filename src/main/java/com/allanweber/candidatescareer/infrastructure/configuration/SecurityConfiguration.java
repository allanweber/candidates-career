package com.allanweber.candidatescareer.infrastructure.configuration;

import com.allanweber.candidatescareer.domain.auth.jwt.AuthEntryPointJwt;
import com.allanweber.candidatescareer.domain.auth.jwt.JwtAuthorizationFilter;
import com.allanweber.candidatescareer.domain.user.AuthoritiesHelper;
import com.allanweber.candidatescareer.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.http.HttpMethod.OPTIONS;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] PUBLIC_PATH = {
            "/health",
            "/auth/login", "/registration/**", "/social-authorization/**", "/auth/callback/**",
            "/", "/v2/api-docs", "/swagger*/**", "/webjars/**"};

    private static final String[] ADMIN_PATH = {"/admin/**", "/users/**"};

    private final UserService userService;
    private final PasswordEncoder encoder;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .authorizeRequests()
                .antMatchers(OPTIONS, "/**").permitAll()
                .antMatchers(PUBLIC_PATH).permitAll()
                .antMatchers(ADMIN_PATH).hasRole(AuthoritiesHelper.ADMIN)
                .anyRequest().hasRole(AuthoritiesHelper.USER).and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.cors();
        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(encoder);
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
