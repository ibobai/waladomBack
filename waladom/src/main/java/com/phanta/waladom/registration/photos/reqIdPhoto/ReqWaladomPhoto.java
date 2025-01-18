package com.phanta.waladom.registration.photos.reqIdPhoto;

import com.phanta.waladom.base.BasePhoto;
import com.phanta.waladom.registration.RegistrationRequest;
import jakarta.persistence.*;

@Entity
@Table(name = "req_waladom_id_photos")
public class ReqWaladomPhoto extends BasePhoto {
    @Override
    protected String generatePrefix() {
        return "REQWLDP_";
    }
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_request_id", nullable = false, unique = true)
    private RegistrationRequest registrationRequest;

    public RegistrationRequest getRegistrationRequest() {
        return registrationRequest;
    }

    public void setRegistrationRequest(RegistrationRequest registrationRequest) {
        this.registrationRequest = registrationRequest;
    }
}
