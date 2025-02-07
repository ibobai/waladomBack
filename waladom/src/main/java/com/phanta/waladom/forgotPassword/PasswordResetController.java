package com.phanta.waladom.forgotPassword;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user/password")
public class PasswordResetController {

    private static final Logger logger = LogManager.getLogger(PasswordResetController.class);
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/reset")
    public ResponseEntity<Map<String, Object>> requestPasswordReset(
            @RequestHeader("identifier") String identifier,
            @RequestHeader("Connection-Method") String connectionMethod) {

        logger.info("Received password reset request for identifier: {}, connectionMethod: {}", identifier, connectionMethod);

        if (!connectionMethod.equalsIgnoreCase("email") && !connectionMethod.equalsIgnoreCase("phone")) {
            logger.warn("Invalid connection method provided: {}", connectionMethod);
            return ResponseEntity.badRequest().body(Map.of(
                    "send", false,
                    "message", "Unknown connection method"
            ));
        }
        if(connectionMethod.equalsIgnoreCase("email")){
            identifier = identifier.toLowerCase();
        }
        Map<String, Object> response = passwordResetService.sendCode(identifier, connectionMethod);

        logger.info("Password reset request processed for identifier: {}, response: {}", identifier, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateResetCode(
            @RequestHeader("identifier") String identifier,
            @RequestHeader("Connection-Method") String connectionMethod,
            @RequestHeader("code") String code) {

        logger.info("Received reset code validation request for identifier: {}, connectionMethod: {}, code: {}", identifier, connectionMethod, code);

        if (!connectionMethod.equalsIgnoreCase("email") && !connectionMethod.equalsIgnoreCase("phone")) {
            logger.warn("Invalid connection method provided: {}", connectionMethod);
            return ResponseEntity.badRequest().body(Map.of(
                    "verified", false,
                    "message", "Unknown connection method"
            ));
        }

        if(connectionMethod.equalsIgnoreCase("email")){
            identifier = identifier.toLowerCase();
        }
        Map<String, Object> response = passwordResetService.validateCode(identifier, connectionMethod, code);

        logger.info("Reset code validation result for identifier: {}, response: {}", identifier, response);
        return ResponseEntity.ok(response);
    }
}
