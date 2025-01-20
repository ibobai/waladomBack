package com.phanta.waladom.registration.photos.reqIdProof;

import com.phanta.waladom.registration.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReqIdProofRepository extends JpaRepository<ReqIdProof, String>{
    Optional<ReqIdProof> findByRegistrationRequestAndPhotoType(RegistrationRequest registrationRequest, String photoType);

}

