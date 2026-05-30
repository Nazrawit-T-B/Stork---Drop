package com.minStork.Stork.data;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByAuthToken(String authToken);
    @Modifying //tells Spring this is a write query not update
    @Transactional // ensures it commits clearly
    @Query("UPDATE UserEntity u SET u.authToken= :token WHERE u.id= :id")
    void updateAuthToken(@Param("id") Long id, @Param("token") String token);


}
