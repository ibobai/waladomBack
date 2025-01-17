package com.phanta.waladom.idProof;

import com.phanta.waladom.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdPhotoProofRepository extends JpaRepository<IdProofPhoto, String> {
    Optional<IdProofPhoto> findByUserAndPhotoType(User user, String photoType);
}
