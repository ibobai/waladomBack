package com.phanta.waladom.registration;

import com.phanta.waladom.base.BaseUser;
import com.phanta.waladom.registration.photos.reqIdPhoto.ReqWaladomPhoto;
import com.phanta.waladom.registration.photos.reqIdProof.ReqIdProof;
import com.phanta.waladom.role.Role;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "registration_requests")
public class RegistrationRequest extends BaseUser {

    @Column(name = "VALIDATED", nullable = false)
    private Boolean validated = false; // Specific to registration requests

    //Relations
    @OneToOne(mappedBy = "registrationRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ReqWaladomPhoto reqWaladomPhoto;

    @OneToMany(mappedBy = "registrationRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReqIdProof> idProofPhotos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;


    @Override
    protected String getIdPrefix() {
        return "RGST_";
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public ReqWaladomPhoto getReqWaladomPhoto() {
        return reqWaladomPhoto;
    }

    public void setReqWaladomPhoto(ReqWaladomPhoto reqWaladomPhoto) {
        this.reqWaladomPhoto = reqWaladomPhoto;
    }

    public List<ReqIdProof> getReqIdProofPhotos() {
        return idProofPhotos;
    }

    public void setReqIdProofPhotos(List<ReqIdProof> idProofPhotos) {
        this.idProofPhotos = idProofPhotos;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}