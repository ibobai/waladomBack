package com.phanta.waladom.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.waladomIdPhoto " +
            "LEFT JOIN FETCH u.idProofPhotos " +
            "LEFT JOIN FETCH u.role " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithDetails(@Param("id") String id);
    @Query("SELECT u FROM User u WHERE (u.email = :identifier OR u.phone = :identifier) AND u.connectionMethod = :connectionMethod")
    Optional<User> findByEmailOrPhoneAndIdentifier(@Param("identifier") String identifier, @Param("connectionMethod") String connectionMethod);


      @Query("SELECT rr FROM User rr LEFT JOIN FETCH rr.waladomIdPhoto LEFT JOIN FETCH rr.idProofPhotos LEFT JOIN FETCH rr.role")
      List<User> findAllWithAssociations();

    @Query("SELECT u FROM User u WHERE " +
            "(:id IS NULL OR u.id = :id) AND " +
            "(:firstName IS NULL OR u.firstName LIKE %:firstName%) AND " +
            "(:lastName IS NULL OR u.lastName LIKE %:lastName%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:phone IS NULL OR u.phone LIKE %:phone%)")
    List<User> searchUsers(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("id") String id,
            @Param("email") String email,
            @Param("phone") String phone
    );


}