package com.phanta.waladom.verification.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, String> {
    EmailVerificationCode findByEmail(String email);
    EmailVerificationCode findByVerificationCode(String verificationCode);
    EmailVerificationCode findByVerificationCodeAndEmail(String verificationCode, String email);
}
