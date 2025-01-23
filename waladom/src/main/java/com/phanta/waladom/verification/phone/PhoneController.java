package com.phanta.waladom.verification.phone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/verification/phone")
public class PhoneController {

    @Autowired
    private final PhoneVerificationService  phoneVerificationService;

    @Autowired
    public PhoneController(PhoneVerificationService phoneVerificationService) {
        this.phoneVerificationService = phoneVerificationService;
    }


    // Endpoint to send the verification code to the user
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phone");

        if (phoneNumber == null) {
            return ResponseEntity.badRequest().body(Map.of("send", false, "message", "Phone number is required"));
        }

        Map<String, Object> response = phoneVerificationService.sendVerificationCode(phoneNumber);
        return ResponseEntity.ok(response);
    }

    // Endpoint to verify the entered code
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String phoneNumber = request.get("phone");

        if (code == null || phoneNumber == null) {
            return ResponseEntity.badRequest().body(Map.of("verified", false, "message", "Code and phone number are required"));
        }

        Map<String, Object> response = phoneVerificationService.verifyCode(code, phoneNumber);
        return ResponseEntity.ok(response);
    }
}
