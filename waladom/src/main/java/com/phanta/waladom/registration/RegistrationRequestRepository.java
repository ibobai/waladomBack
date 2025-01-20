package com.phanta.waladom.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, String> {
    Optional<RegistrationRequest> findByEmail(String email);
    @Query("SELECT rr FROM RegistrationRequest rr LEFT JOIN FETCH rr.reqWaladomIdPhoto LEFT JOIN FETCH rr.reqIdProofPhotos LEFT JOIN FETCH rr.role")
    List<RegistrationRequest> findAllWithAssociations();


}
