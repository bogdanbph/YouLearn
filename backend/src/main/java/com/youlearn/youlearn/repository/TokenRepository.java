package com.youlearn.youlearn.repository;

import com.youlearn.youlearn.model.Token;
import com.youlearn.youlearn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE Token t " +
            "SET t.confirmedAt = ?2 " +
            "WHERE t.token = ?1")
    int updateConfirmedAt(String token, LocalDateTime now);

    void deleteByUserId(Long userId);
}
