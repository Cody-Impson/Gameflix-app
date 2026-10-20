package com.example.gameflixauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMs = expirationMs;

        this.verifier = JWT.require(algorithm)
                .withIssuer("gameflix-auth")
                .build();
    }

    public String generateToken(String email) {

        Instant now = Instant.now();

        return JWT.create()
                .withIssuer("gameflix-auth")
                .withSubject(email)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(expirationMs)))
                .sign(algorithm);
    }

    public String validateAndGetEmail(String token) {
        return verifier.verify(token).getSubject();
    }
}