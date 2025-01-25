package com.phanta.waladom.user;

import com.phanta.waladom.base.BaseUser;
import com.phanta.waladom.idPhoto.WaladomIdPhoto;
import com.phanta.waladom.idProof.IdProofPhoto;
import com.phanta.waladom.role.Role;
import com.phanta.waladom.utiles.UtilesMethods;
import jakarta.persistence.*;

import java.util.List;
import java.util.Random;

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




    public static String generateUserId(String gender, int birthYear, int birthMonth, String country, int joiningYear) {
        StringBuilder userId = new StringBuilder("WLD_");

        // Gender: M -> 1, F -> 2, O -> 3
        switch (gender.toUpperCase()) {
            case "M" -> userId.append("1");
            case "F" -> userId.append("2");
            default -> userId.append("3");
        }

        // Add birth month (two digits)
        userId.append(String.format("%02d", birthMonth));

        // Add last two digits of birth year
        userId.append(String.format("%02d", birthYear % 100));

        // Add country code (e.g., 249 for Sudan)
        userId.append(UtilesMethods.getCountryCode(country));

        // Add last two digits of joining year
        userId.append(String.format("%02d", joiningYear % 100));

        // Add two random digits
        Random random = new Random();
        userId.append(random.nextInt(10)).append(random.nextInt(10));

        return userId.toString();
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
