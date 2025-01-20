package com.phanta.waladom.registration.photos.reqIdPhoto;

import com.phanta.waladom.registration.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReqWaladomPhotoRepository extends JpaRepository<ReqWaladomIdPhoto, String> {
    Optional<ReqWaladomIdPhoto> findByRegistrationRequest(RegistrationRequest registrationRequest);

}
