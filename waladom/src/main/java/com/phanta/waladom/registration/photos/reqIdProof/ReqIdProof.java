package com.phanta.waladom.registration.photos.reqIdProof;

import com.phanta.waladom.base.BasePhoto;
import com.phanta.waladom.registration.RegistrationRequest;
import jakarta.persistence.*;

@Entity
@Table(name = "req_id_proof_photos")
public class ReqIdProof extends BasePhoto {
    @Override
    protected String generatePrefix() {
        return "REQPRF_";
    }
    @Column(name = "PHOTO_TYPE", length = 20, nullable = false)
    private String photoType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_request_id", nullable = false, unique = true)
    private RegistrationRequest registrationRequest;


    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    public RegistrationRequest getRegistrationRequest() {
        return registrationRequest;
    }

    public void setRegistrationRequest(RegistrationRequest registrationRequest) {
        this.registrationRequest = registrationRequest;
    }
}
