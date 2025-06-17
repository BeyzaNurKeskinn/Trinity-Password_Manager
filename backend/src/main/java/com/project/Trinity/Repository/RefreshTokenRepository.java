
package com.project.Trinity.Repository;

import com.project.Trinity.Entity.RefreshToken;
import com.project.Trinity.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
    void deleteByUserId(Long id);
    void deleteByExpiryDateBefore(LocalDateTime date);
}//Refresh token’lar için veritabanı işlemlerini sağlar.Veritabanı işlemlerini kolaylaştırır.

//Özel sorgular: Token’a, kullanıcıya göre bulma ve silme.