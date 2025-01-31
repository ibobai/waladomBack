package com.phanta.waladom.verification.email;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_verification_codes") // Specify the table name here
@Data
@AllArgsConstructor
public class EmailVerificationCode {


    @Id
    @Column(name = "id", length = 50, nullable = false, unique = true)
    private String id = "EMVRF_" + UUID.randomUUID();

    @Column(name = "email", nullable = false) // Ensures email is not null
    private String email;

    @Column(name = "verification_code", nullable = false) // Column name for verificationCode
    private String verificationCode;

    @Column(name = "created_at", nullable = false) // Column name for createdAt
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false) // Column name for expiresAt
    private Instant expiresAt;

    @Column(name = "is_verified", nullable = false) // Column name for isVerified
    private boolean isVerified;


    // Constructors
    public EmailVerificationCode() {
        // Default constructor
    }

    public EmailVerificationCode(String email, String verificationCode, Instant createdAt, Instant expiresAt, boolean isVerified) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isVerified = isVerified;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
