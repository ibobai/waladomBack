package com.phanta.waladom.forgotPassword;

import com.phanta.waladom.user.User;
import com.phanta.waladom.user.UserRepository;
import com.phanta.waladom.verification.email.EmailService;
import com.phanta.waladom.verification.phone.PhoneVerificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final Logger logger = LogManager.getLogger(PasswordResetService.class);
    private static final int CODE_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PhoneVerificationService phoneVerificationService;

    public PasswordResetService(PasswordResetRepository passwordResetRepository, UserRepository userRepository, EmailService emailService, PhoneVerificationService phoneVerificationService) {
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.phoneVerificationService = phoneVerificationService;
    }

    public Map<String, Object> sendCode(String identifier, String connectionMethod) {
        logger.info("Initiating password reset for identifier: {}, method: {}", identifier, connectionMethod);

        Optional<User> user = userRepository.findByEmailOrPhoneAndIdentifier(identifier, connectionMethod);
        if (user.isEmpty()) {
            logger.warn("No user found with identifier: {} using method: {}", identifier, connectionMethod);
            return Map.of("sent", false, "message", "No user found with this " + connectionMethod);
        }

        if (!user.get().getActive() || !(user.get().getStatus().equalsIgnoreCase("active"))) {
            logger.warn("Inactive user attempted password reset: {}", identifier);
            return Map.of("sent", false, "message", "This user is not active! Could be blocked or banned.");
        }

        Optional<PasswordReset> alreadyExist = passwordResetRepository.findByIdentifier(identifier);
        if (alreadyExist.isPresent()) {
            logger.warn("Password reset already requested for identifier: {}", identifier);
            return Map.of("sent", false, "message", "There is already a password reset request for this identifier!.");
        }

        String code = emailService.generateCode();
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(CODE_EXPIRY_TIME);

        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setVerificationCode(code);
        passwordReset.setCreatedAt(now);
        passwordReset.setExpiresAt(expiresAt);
        passwordReset.setIdentifier(identifier);

        Map<String, Object> response;
        if ("phone".equalsIgnoreCase(connectionMethod)) {
            logger.info("Sending password reset code via SMS to: {}", identifier);
            response = phoneVerificationService.sendSMS(identifier, code);
        } else if ("email".equalsIgnoreCase(connectionMethod)) {
            logger.info("Sending password reset code via Email to: {}", identifier);
            response = emailService.sendEmail(identifier, code);
        } else {
            logger.error("Unknown connection method provided: {}", connectionMethod);
            return Map.of("sent", false, "message", "Unknown connection method!");
        }

        if ((Boolean) response.get("send")) {
            passwordResetRepository.save(passwordReset);
            logger.info("Password reset request successfully created for identifier: {}", identifier);
        } else {
            logger.error("Failed to send password reset code to: {}", identifier);
        }

        return response;
    }

    public Map<String, Object> validateCode(String identifier, String connectionMethod, String code) {
        logger.info("Validating reset code for identifier: {}, method: {}", identifier, connectionMethod);

        Optional<PasswordReset> passwordReset = passwordResetRepository.findByIdentifier(identifier);
        if (passwordReset.isEmpty()) {
            logger.warn("No password reset request found for identifier: {}", identifier);
            return Map.of("verified", false, "message", "No password reset request found with this identifier: " + identifier);
        }

        if (code != null) {
            Instant now = Instant.now();

            if (passwordReset.get().getVerificationCode().equals(code)) {
                if (passwordReset.get().getExpiresAt().isBefore(now)) {
                    passwordResetRepository.delete(passwordReset.get());
                    logger.warn("Expired reset code used for identifier: {}", identifier);
                    return Map.of("verified", false, "message", "Code has expired.");
                }
                passwordResetRepository.delete(passwordReset.get());
                logger.info("Successfully validated reset code for identifier: {}", identifier);
                return Map.of("verified", true, "message", "Verification OK!.");
            }
        }

        logger.warn("Invalid reset code provided for identifier: {}", identifier);
        return Map.of("verified", false, "message", "Invalid code.");
    }
}
