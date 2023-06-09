package com.ph.teamappbackend.utils;

/**
 * @author octopus
 * @since 2023/4/15 20:56
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ph.teamappbackend.pojo.entity.UserEntity;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class JwtUtils {

    private final static String SECRET_KEY = "d6a657ed-40c2-4dfb-90f8-6551823af298";

    public static String getToken(UserEntity u) {
        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("userId", u.getId())
                .withClaim("deviceId", UUID.randomUUID().toString())
                .withClaim("loginTimestamp", System.currentTimeMillis())
                .withClaim("username", u.getUsername());

        return builder.sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public static DecodedJWT verify(String token) {
        if (!StringUtils.hasText(token)) {
            throw new RuntimeException("Empty token.");
        }
        JWTVerifier build = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
        try {
            return build.verify(token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token.");
        }
    }

}

