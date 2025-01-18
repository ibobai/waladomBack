package com.phanta.waladom.idProof;


import com.phanta.waladom.base.BasePhoto;
import com.phanta.waladom.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "id_proof_photos") // Use the actual table name in your database
public class IdProofPhoto extends BasePhoto {

    @Column(name = "PHOTO_TYPE", length = 20, nullable = false)
    private String photoType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Override
    protected String generatePrefix() {
        return "IDPH_"; // Prefix for ID proof photos
    }

    // Getter and Setter for photoType
    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
