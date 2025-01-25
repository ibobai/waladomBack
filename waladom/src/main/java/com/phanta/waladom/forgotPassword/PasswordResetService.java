package com.phanta.waladom.forgotPassword;

import com.phanta.waladom.user.User;
import com.phanta.waladom.user.UserRepository;
import com.phanta.waladom.verification.email.EmailService;
import com.phanta.waladom.verification.phone.PhoneVerificationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PasswordResetService {

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

        Map<String, Object> response = new HashMap<>();

        //check if user exists
        Optional<User> user = userRepository.findByEmailOrPhoneAndIdentifier(identifier, connectionMethod);
        if (user.isEmpty()) {
            return Map.of("sent", false,
                    "message", "No user found with this " + connectionMethod);
        }

        //then user exists
        if (!user.get().getActive() || !(user.get().getStatus().equalsIgnoreCase("active"))) {
            return Map.of("sent", false,
                    "message", "This user is not active! Could be blocked or banned.");
        }

        //check if passwordRest demand is already exists
        Optional<PasswordReset> alreadyExist = passwordResetRepository.findByIdentifier(identifier);
        if(alreadyExist.isPresent()){
            return Map.of("sent", false,
                    "message", "There is already a password rest demand for this identifier!.");

        }

        //generate the code and other fields
        String code = emailService.generateCode();
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(CODE_EXPIRY_TIME);

        //save the code
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setVerificationCode(code);
        passwordReset.setCreatedAt(now);
        passwordReset.setExpiresAt(expiresAt);
        passwordReset.setIdentifier(identifier);


        //user exists and active, then send the code
        if ("phone".equalsIgnoreCase(connectionMethod)) {
            response = phoneVerificationService.sendSMS(identifier, code);
        } else if ("email".equalsIgnoreCase(connectionMethod)) {
            response = emailService.sendEmail(identifier, code);
        } else {
            return Map.of("sent", false,
                    "message", "Unknown connection method !");
        }
        if((Boolean) response.get("send")){
            passwordResetRepository.save(passwordReset);
        }

        return response;
    }




    public Map<String, Object> validateCode(String identifier, String connectionMethod, String code) {

        Optional<PasswordReset> passwordReset = passwordResetRepository.findByIdentifier(identifier);

        if (passwordReset.isEmpty()) {
            return Map.of("verified", false,
                    "message", "No password rest request found with this identifier : " + identifier);
        }

        if (code != null) {
            Instant now = Instant.now();

            if (passwordReset.get().getExpiresAt().isBefore(now)) {
                return Map.of("verified", false, "message", "Code has expired.");
            }

            if (passwordReset.get().getVerificationCode().equals(code)) {
                passwordResetRepository.delete(passwordReset.get());
                return Map.of("verified", true, "message", "Verification OK!.");
            }

        }
        return Map.of("verified", false, "message", "Invalid code.");

    }
}

