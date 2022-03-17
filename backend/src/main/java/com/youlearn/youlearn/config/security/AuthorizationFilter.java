package com.youlearn.youlearn.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.youlearn.youlearn.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    @Value("${application.jwt.secretKey}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();

                DecodedJWT decodedJWT = jwtVerifier.verify(token);

                String username = decodedJWT.getSubject();
                String[] roles = decodedJWT.getClaim("role").asArray(String.class);

                Collection<SimpleGrantedAuthority> authorityCollection = new ArrayList<>();
                Arrays.stream(roles).forEach(role -> authorityCollection.add(new SimpleGrantedAuthority(role)));

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorityCollection);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                filterChain.doFilter(request, response);
            }
            catch (Exception ex) {
                throw new BadRequestException(ex.getMessage());
            }
        }
        else {
            filterChain.doFilter(request, response);
        }
    }
}
