package com.phanta.waladom.registration;

import com.phanta.waladom.base.BaseUser;
import com.phanta.waladom.registration.photos.reqIdPhoto.ReqWaladomIdPhoto;
import com.phanta.waladom.registration.photos.reqIdProof.ReqIdProof;
import com.phanta.waladom.role.Role;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "registration_requests")
public class RegistrationRequest extends BaseUser {

    @Column(name = "VALIDATED", nullable = false)
    private Boolean validated = false; // Specific to registration requests

    @Id
    @Column(name = "ID", length = 50, nullable = false, unique = true)
    private String id;

    //Relations
    @OneToOne(mappedBy = "registrationRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ReqWaladomIdPhoto reqWaladomIdPhoto;

    @OneToMany(mappedBy = "registrationRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReqIdProof> reqIdProofPhotos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;


    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = getIdPrefix() + UUID.randomUUID();
        }
    }

    @Override
    protected String getIdPrefix() {
        return "RGST_";
    }

      public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReqWaladomIdPhoto getReqWaladomIdPhoto() {
        return reqWaladomIdPhoto;
    }

    public void setReqWaladomIdPhoto(ReqWaladomIdPhoto reqWaladomIdPhoto) {
        this.reqWaladomIdPhoto = reqWaladomIdPhoto;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public ReqWaladomIdPhoto getReqWaladomPhoto() {
        return reqWaladomIdPhoto;
    }

    public void setReqWaladomPhoto(ReqWaladomIdPhoto reqWaladomIdPhoto) {
        this.reqWaladomIdPhoto = reqWaladomIdPhoto;
    }

    public List<ReqIdProof> getReqIdProofPhotos() {
        return reqIdProofPhotos;
    }

    public void setReqIdProofPhotos(List<ReqIdProof> idProofPhotos) {
        this.reqIdProofPhotos = idProofPhotos;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}