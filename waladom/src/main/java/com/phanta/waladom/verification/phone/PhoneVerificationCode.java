package com.phanta.waladom.verification.phone;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "phone_verification_codes") // Specify the table name here
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneVerificationCode {

    @Id
    @Column(name = "id", length = 50, nullable = false, unique = true)
    private String id = "PHVRF_" + UUID.randomUUID();

    @Column(name = "phone_number", nullable = false) // Ensures phone number is not null
    private String phoneNumber;

    @Column(name = "verification_code", nullable = false) // Column name for verificationCode
    private String verificationCode;

    @Column(name = "created_at", nullable = false) // Column name for createdAt
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false) // Column name for expiresAt
    private Instant expiresAt;

    @Column(name = "is_verified", nullable = false) // Column name for isVerified
    private boolean isVerified;

    // Constructors
    public PhoneVerificationCode(String phoneNumber, String verificationCode, Instant createdAt, Instant expiresAt, boolean isVerified) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isVerified = isVerified;
    }

    public PhoneVerificationCode(){

    }
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
