package com.phanta.waladom.idPhoto;

import com.phanta.waladom.base.BasePhoto;
import com.phanta.waladom.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "waladom_id_photos") // Use the actual table name in your database
public class WaladomIdPhoto extends BasePhoto {

    @Override
    protected String generatePrefix() {
        return "WLDPH_"; // Prefix for Waladom ID photos
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
