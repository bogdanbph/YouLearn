package com.youlearn.youlearn.repository;

import com.youlearn.youlearn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);

    @javax.transaction.Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.isEnabled = TRUE WHERE u.email = ?1")
    void enableAppUser(String email);
}
