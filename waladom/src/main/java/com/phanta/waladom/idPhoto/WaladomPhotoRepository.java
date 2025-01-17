package com.phanta.waladom.idPhoto;

import com.phanta.waladom.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaladomPhotoRepository extends JpaRepository<WaladomIdPhoto, String> {
    Optional<WaladomIdPhoto> findByUser(User user);
}
