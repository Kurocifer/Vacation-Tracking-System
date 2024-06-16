package com.vts.vaccation_tracking_system.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.vts.vaccation_tracking_system.model.AbstractUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("{$jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;
    private Algorithm algorithm;
    private static final String USERNAME_KEY = "USERNAME";
    private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";

    @PostConstruct
    public void postConstruct() throws UnsupportedEncodingException {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateJWT(AbstractUser user) {
        System.out.println("generate JWT, username: " + user.getUsername());
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getUsername(String token) {
        return JWT.decode(token).getClaim(USERNAME_KEY).asString();
    }

    public String getVerificationUsername(String token) {
        return JWT.decode(token).getClaim(VERIFICATION_EMAIL_KEY).asString();
    }

    public String generateVerificationJWT(AbstractUser user) {
        return JWT.create()
                .withClaim(VERIFICATION_EMAIL_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }
}
