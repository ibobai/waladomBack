package com.phanta.waladom.oauthToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        logger.info("Processing request URI: {}", request.getRequestURI());

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Extract token from the Authorization header
            logger.debug("Extracted token: {}", token);

            try {
                // Validate token and extract details
                if (jwtTokenUtil.validateToken(token)) {
                    String email = jwtTokenUtil.extractEmail(token);  // Extract email from the token
                    String role = jwtTokenUtil.extractRole(token);    // Extract role from the token

                    logger.info("Token is valid. Email: {}, Role: {}", email, role);

                    // Add "ROLE_" prefix to role to match Spring Security's default behavior
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email, null, List.of(() -> "ROLE_" + role));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.info("User '{}' authenticated successfully with role: ROLE_{}", email, role);
                } else {
                    logger.warn("Invalid token: {}", token);
                    throw new AuthenticationServiceException("Invalid token");
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                logger.error("Authentication failed: {}", e.getMessage(), e);
                throw new AuthenticationServiceException("Authentication failed: " + e.getMessage(), e);
            }
        } else {
            if (authHeader == null) {
                logger.debug("No Authorization header present in the request");
            } else {
                logger.warn("Authorization header does not start with 'Bearer ': {}", authHeader);
            }
        }

        // Continue filter chain
        logger.debug("Proceeding with the filter chain for request: {}", request.getRequestURI());
        chain.doFilter(request, response);
    }
}
