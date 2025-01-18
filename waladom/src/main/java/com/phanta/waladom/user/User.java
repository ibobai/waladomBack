package com.phanta.waladom.user;

import com.phanta.waladom.base.BaseUser;
import com.phanta.waladom.idPhoto.WaladomIdPhoto;
import com.phanta.waladom.idProof.IdProofPhoto;
import com.phanta.waladom.role.Role;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "USERS")
public class User extends BaseUser  {


    //Relations
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private WaladomIdPhoto waladomIdPhoto;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IdProofPhoto> idProofPhotos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;



    @Override
    protected String getIdPrefix() {
        return "WLD_";
    }


    // Getters and Setters
    public User() {
    }

    public WaladomIdPhoto getWaladomIdPhoto() {
        return waladomIdPhoto;
    }

    public void setWaladomIdPhoto(WaladomIdPhoto waladomIdPhoto) {
        this.waladomIdPhoto = waladomIdPhoto;
    }

    public List<IdProofPhoto> getIdProofPhotos() {
        return idProofPhotos;
    }

    public void setIdProofPhotos(List<IdProofPhoto> idProofPhotos) {
        this.idProofPhotos = idProofPhotos;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
