package com.fanni.expense_tracker.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if(authorizationHeader == null || authorizationHeader.length()==0 || !authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else{
            String token = authorizationHeader.replace("Bearer ", "");
            try{
                String key = "VeeerySecureKeyWhichShallBeMoreThan256BitsAsByteArraySoIDontGetWeakKeyException";

                JwtParser jwtParser = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(key.getBytes()))
                        .build();

                Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
                Claims body = claimsJws.getBody();
                String username = body.getSubject();
                var authorities = (List<Map<String, String>>) body.get("authorities");

                Set<SimpleGrantedAuthority> simpleGrantedAuthoritites = authorities.stream().map(stringStringMap -> new SimpleGrantedAuthority(stringStringMap.get("authority")))
                        .collect(Collectors.toSet());

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        simpleGrantedAuthoritites
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch(JwtException jwtException){
                throw new IllegalStateException(String.format("Token %s cannot be trusted", token));
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}
