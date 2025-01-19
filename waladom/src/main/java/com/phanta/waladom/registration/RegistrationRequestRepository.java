package com.phanta.waladom.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, String> {
    Optional<RegistrationRequest> findByEmail(String email);


}
