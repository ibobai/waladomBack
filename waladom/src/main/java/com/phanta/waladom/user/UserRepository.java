package com.phanta.waladom.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.waladomIdPhoto " +
            "LEFT JOIN FETCH u.idProofPhotos " +
            "LEFT JOIN FETCH u.role " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithDetails(@Param("id") String id);
}