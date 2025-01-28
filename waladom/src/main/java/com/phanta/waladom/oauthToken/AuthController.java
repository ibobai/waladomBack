package com.phanta.waladom.oauthToken;

import com.phanta.waladom.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @Autowired
    public AuthController(JwtTokenUtil jwtTokenUtil, UserService userService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    /**
     * Login endpoint to authenticate the user using Basic Auth and return tokens.
     *
     * @return ResponseEntity with login status and tokens if successful
     */
    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> login(HttpServletRequest request) {
        logger.info("Received login request.");

        String authorizationHeader = request.getHeader("Authorization");
        String connectionMethod = request.getHeader("Connection-Method");

        Map<String, Object> response = new HashMap<>();

        if (connectionMethod == null || connectionMethod.isEmpty()) {
            logger.warn("Missing 'Connection-Method' header.");
            response.put("valid", false);
            response.put("message", "Missing required header: Connection-Method");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            logger.warn("Invalid or missing 'Authorization' header.");
            response.put("valid", false);
            response.put("message", "Invalid or missing Authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (!"email".equalsIgnoreCase(connectionMethod) && !"phone".equalsIgnoreCase(connectionMethod)) {
            logger.warn("Invalid connection method: {}", connectionMethod);
            response.put("valid", false);
            response.put("message", "Invalid connection method. Use 'email' or 'phone'.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            String base64Credentials = authorizationHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);

            if (values.length == 2) {
                String identifier = values[0];
                String password = values[1];

                logger.info("Attempting login for identifier: {} using connection method: {}", identifier, connectionMethod);
                return userService.login(identifier, password, connectionMethod.toLowerCase());
            } else {
                logger.error("Invalid credentials format in 'Authorization' header.");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Failed to decode credentials from 'Authorization' header: {}", e.getMessage());
        }

        response.put("valid", false);
        response.put("message", "Invalid or missing Authorization header");
        logger.warn("Login request failed due to invalid credentials.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Refresh Token Endpoint to generate a new access token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authorizationHeader) {
        logger.info("Received token refresh request.");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Invalid or missing 'Authorization' header in refresh request.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        }

        String refreshToken = authorizationHeader.substring(7);
        logger.info("Validating refresh token.");

        if (jwtTokenUtil.validateToken(refreshToken)) {
            logger.info("Refresh token is valid.");

            String email = jwtTokenUtil.extractEmail(refreshToken);
            String role = jwtTokenUtil.extractRole(refreshToken);

            logger.info("Generating new access token for email: {} and role: {}", email, role);
            String newAccessToken = jwtTokenUtil.generateAccessToken(email, role);

            logger.info("Successfully generated new access token.");
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        }

        logger.warn("Invalid refresh token.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }
}
