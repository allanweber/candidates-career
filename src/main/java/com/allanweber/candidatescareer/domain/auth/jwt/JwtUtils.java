package com.allanweber.candidatescareer.domain.auth.jwt;

import com.allanweber.candidatescareer.domain.helper.DateHelper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {
    private static final SecretKey SECRETKEY = Keys.hmacShaKeyFor(JwtConstantsHelper.JWT_SECRET.getBytes());

    public TokenDto generateJwtToken(String user, List<String> roles) {

        Date issuedAt = DateHelper.getUTCDatetimeAsDate();

        String token = Jwts.builder()
                .signWith(SECRETKEY, SignatureAlgorithm.HS512)
                .setHeaderParam(JwtConstantsHelper.HEADER_TYP, JwtConstantsHelper.TOKEN_TYPE)
                .setIssuer(JwtConstantsHelper.TOKEN_ISSUER)
                .setAudience(JwtConstantsHelper.TOKEN_AUDIENCE)
                .setSubject(user)
                .setIssuedAt(issuedAt)
                .setExpiration(getExpirationDate())
                .claim(JwtConstantsHelper.AUTHORITIES, roles)
                .compact();

        return new TokenDto(token, JwtConstantsHelper.TOKEN_DURATION_SECONDS, issuedAt);
    }

    @SuppressWarnings("PMD")
    public Jws<Claims> parseAndValidateToken(String token) throws HttpClientErrorException {
        try {
            return Jwts.parser().setSigningKey(JwtConstantsHelper.JWT_SECRET.getBytes()).parseClaimsJws(resolveToken(token));
        } catch (ExpiredJwtException exception) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse expired JWT : %s failed : %s", token, exception.getMessage()));
        } catch (UnsupportedJwtException exception) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse unsupported JWT : %s failed : %s", token, exception.getMessage()));
        } catch (MalformedJwtException exception) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse invalid JWT : %s failed : %s", token, exception.getMessage()));
        } catch (SignatureException exception) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse JWT with invalid signature : %s failed : %s", token, exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse empty or null JWT : %s failed : %s", token, exception.getMessage()));
        }
    }

    public String getUserName(Jws<Claims> token) {
        return token.getBody().getSubject();
    }

    public List<SimpleGrantedAuthority> getAuthorities(Jws<Claims> token) {
        return ((List<?>) token.getBody()
                .get(JwtConstantsHelper.AUTHORITIES)).stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());
    }

    public TokenDto refreshToken(String bearerStr) {
        String token = resolveToken(bearerStr);
        if(StringUtils.isEmpty(token)) {
            throw new AuthorizationServiceException("Invalid token");
        }
        TokenDto newToken = refreshTokenInternal(token);
        log.info("Jwt Token refreshed.");
        return newToken;
    }

    public List<String> getRoles(String token) {
        var jwsClaims = parseAndValidateToken(token);
        return getAuthorities(jwsClaims)
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private TokenDto refreshTokenInternal(String token) {
        if(token == null) {
            throw new AuthorizationServiceException("Invalid token claims");
        }
        var jwsClaims = parseAndValidateToken(token);
        Date issuedAt = DateHelper.getUTCDatetimeAsDate();
        Claims claims = jwsClaims.getBody();
        claims.setIssuedAt(issuedAt);
        claims.setExpiration(getExpirationDate());
        String newToken = Jwts.builder().setClaims(claims).signWith(SECRETKEY, SignatureAlgorithm.HS256).compact();
        return new TokenDto(newToken, JwtConstantsHelper.TOKEN_DURATION_SECONDS, issuedAt);
    }

    private String resolveToken(String bearerHeader) {
        return bearerHeader.replace(JwtConstantsHelper.TOKEN_PREFIX, "");
    }

    private Date getExpirationDate() {
        return Date.from(LocalDateTime.now().plusSeconds(JwtConstantsHelper.TOKEN_DURATION_SECONDS).atZone(ZoneId.systemDefault()).toInstant());
    }
}
