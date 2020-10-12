package com.allanweber.candidatescareer.authentication.auth.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var authentication = getAuthentication(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
        var token = request.getHeader(JwtConstantsHelper.TOKEN_HEADER);
        if (StringUtils.isNotEmpty(token) && token.startsWith(JwtConstantsHelper.TOKEN_PREFIX)) {
            var parsedToken = jwtUtils.parseAndValidateToken(token);
            var username = jwtUtils.getUserName(parsedToken);
            var authorities = jwtUtils.getAuthorities(parsedToken);
            if (StringUtils.isNotEmpty(username)) {
                usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            }
        }
        return usernamePasswordAuthenticationToken;
    }
}
