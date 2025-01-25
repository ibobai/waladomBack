package com.phanta.waladom.verification.phone;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
@Service
public class PhoneVerificationService {

    @Autowired
    private final PhoneVerificationCodeRepository verificationCodeRepository;



    @Value("${twilio.account.sid}")
    private String twilioAccountSid = "ACaf30901b6856b0ea176d94675e9d8fe6";

    @Value("${twilio.auth.token}")
    private String twilioAuthToken = "0db4d85f8f50700cca21f7be99673e2a";

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber = "+13183863465";



    @Autowired
    private final RestTemplate restTemplate;

    private static final int CODE_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Autowired
    public PhoneVerificationService(PhoneVerificationCodeRepository verificationCodeRepository, RestTemplate restTemplate) {
        this.verificationCodeRepository = verificationCodeRepository;
        Twilio.init(twilioAccountSid, twilioAuthToken);
        this.restTemplate = restTemplate;
    }

    // Method to generate a 6-digit random code
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generate 6-digit number
        return String.valueOf(code);
    }

    public Map<String, Object> sendVerificationCode(String phoneNumber) {
        // Check if the phone number already exists in the database
        PhoneVerificationCode existingRecord = verificationCodeRepository.findByPhoneNumber(phoneNumber);

        if (existingRecord != null) {
            if (existingRecord.isVerified()) {
                return Map.of("send", false, "message", "Phone number is already verified. No need to send another code.");
            } else {
                // If the phone number exists but is not verified, update the verification code and expiration
                String newCode = generateCode();
                Instant now = Instant.now();
                Instant expiresAt = now.plusMillis(CODE_EXPIRY_TIME);

                existingRecord.setVerificationCode(newCode);
                existingRecord.setCreatedAt(now);
                existingRecord.setExpiresAt(expiresAt);

                verificationCodeRepository.save(existingRecord);

                return sendSMS(phoneNumber, newCode);
            }
        }

        // Generate a new 6-digit code for a new phone number
        String code = generateCode();
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(CODE_EXPIRY_TIME);

        PhoneVerificationCode verificationCode = new PhoneVerificationCode();
        verificationCode.setPhoneNumber(phoneNumber);
        verificationCode.setVerificationCode(code);
        verificationCode.setCreatedAt(now);
        verificationCode.setExpiresAt(expiresAt);
        verificationCode.setVerified(false);

        verificationCodeRepository.save(verificationCode);

        return sendSMS(phoneNumber, code);
    }



    // Helper method to send the SMS
    public Map<String, Object> sendSMS(String phoneNumber, String code) {
        String messageText = "Please use the following code to verify your phone number: " + code;


        try {

            if(Message.creator(
                            new com.twilio.type.PhoneNumber(phoneNumber),
                            new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                            messageText)
                    .create() != null){

                return Map.of("send", true, "message", "Verification code sent successfully");
            } else {
                return Map.of("send", false, "message", "Failed to send SMS: ");
            }
        } catch (Exception e) {
            return Map.of("send", false, "message", "Error sending SMS: " + e.getMessage());
        }
    }

    public Map<String, Object> verifyCode(String code, String phoneNumber) {
        // Find the verification code record in the database
        PhoneVerificationCode verificationCode = verificationCodeRepository.findByVerificationCodeAndPhoneNumber(code, phoneNumber);

        if (verificationCode != null) {
            Instant now = Instant.now();

            if (verificationCode.isVerified()) {
                return Map.of("verified", false, "message", "Phone number is already verified.");
            }

            if (verificationCode.getExpiresAt().isBefore(now) && verificationCode.getVerificationCode().equals(code)) {
                return Map.of("verified", false, "message", "Code has expired.");
            }

            if (verificationCode.getVerificationCode().equals(code)) {
                verificationCode.setVerified(true);
                verificationCodeRepository.save(verificationCode);
                return Map.of("verified", true, "message", "Phone number verified successfully!");
            }

            return Map.of("verified", false, "message", "Invalid code.");
        }

        return Map.of("verified", false, "message", "No verification request found for this phone number.");
    }

}