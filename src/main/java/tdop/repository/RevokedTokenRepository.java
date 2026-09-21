package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tdop.entity.RevokedToken;
import java.util.Optional;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByTokenHash(String tokenHash);
    Optional<RevokedToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("DELETE FROM RevokedToken r WHERE r.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpired();
}
