package com.phanta.waladom.verification.phone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneVerificationCodeRepository extends JpaRepository<PhoneVerificationCode, Long> {
    PhoneVerificationCode findByPhoneNumber(String phoneNumber);
    PhoneVerificationCode findByVerificationCodeAndPhoneNumber( String verificationCode, String phoneNumber);
}

