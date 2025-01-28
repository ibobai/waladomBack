package com.phanta.waladom.oauthToken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days

    // Generate Access Token
    public String generateAccessToken(String email, String role) {
        logger.info("Generating access token for email: {} with role: {}", email, role);
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY); // Using HMAC256 for signing the JWT

        String token = JWT.create()
                .withSubject(email)  // Subject (username or email)
                .withClaim("role", role)  // Add role as a claim
                .withIssuedAt(new Date())  // Set issue time
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))  // Set expiration time
                .sign(algorithm);  // Sign the token using HMAC256 algorithm

        logger.debug("Access token generated successfully for email: {}", email);
        return token;
    }

    // Generate Refresh Token
    public String generateRefreshToken(String email) {
        logger.info("Generating refresh token for email: {}", email);
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

        String token = JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .sign(algorithm);

        logger.debug("Refresh token generated successfully for email: {}", email);
        return token;
    }

    // Validate Token
    public boolean validateToken(String token) {
        logger.info("Validating token");
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWT.require(algorithm).build().verify(token);  // Verifies token signature
            logger.debug("Token validation successful");
            return true;
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;  // Invalid or expired token
        }
    }

    // Extract Username (email) from the token
    public String extractEmail(String token) {
        logger.info("Extracting email from token");
        try {
            DecodedJWT decodedJWT = JWT.decode(token);  // Decode the token without validating it
            String email = decodedJWT.getSubject();  // Get the subject (username/email)
            logger.debug("Extracted email: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("Failed to extract email from token: {}", e.getMessage());
            throw e;
        }
    }

    // Extract Role from the token
    public String extractRole(String token) {
        logger.info("Extracting role from token");
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String role = decodedJWT.getClaim("role").asString();  // Get role claim
            logger.debug("Extracted role: {}", role);
            return role;
        } catch (Exception e) {
            logger.error("Failed to extract role from token: {}", e.getMessage());
            throw e;
        }
    }
}
