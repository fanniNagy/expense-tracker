package com.fanni.expense_tracker.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    @Autowired
    public JwtTokenVerifier(SecretKey secretKey, JwtConfig jwtConfig) {
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = httpServletRequest.getHeader(jwtConfig.getAuthorizationHeader());
        Cookie[] cookies = httpServletRequest.getCookies();
        Optional<Cookie> authorizationCookie = Optional.empty();
        if (cookies != null) {
            authorizationCookie = Arrays.stream(httpServletRequest.getCookies())
                    .filter(cookie -> cookie.getName().equals(jwtConfig.getAuthorizationHeader()))
                    .findFirst();
        }
        String tokenFromCookie = null;

        if (authorizationCookie.isPresent()) {
            tokenFromCookie = authorizationCookie.get().getValue();
        }

        if (authorizationHeader == null
                || authorizationHeader.length() == 0
                || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())) {
            if (tokenFromCookie != null) {
                tokenFromCookie = tokenFromCookie.replace(jwtConfig.getTokenPrefix(), "");
                tokenFromCookie = tokenFromCookie.replace("Bearer+","");
                verifyToken(tokenFromCookie);
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {

            String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");
            verifyToken(token);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private void verifyToken(String token) {
        try {

            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build();

            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            String username = body.getSubject();
            var authorities = (List<Map<String, String>>) body.get("authorities");

            Set<SimpleGrantedAuthority> simpleGrantedAuthoritites =
                    authorities.stream()
                            .map(stringStringMap -> new SimpleGrantedAuthority(stringStringMap.get("authority")))
                            .collect(Collectors.toSet());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    simpleGrantedAuthoritites
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException jwtException) {
            throw new IllegalStateException(String.format("Token %s cannot be trusted", token));
        }
    }
}
