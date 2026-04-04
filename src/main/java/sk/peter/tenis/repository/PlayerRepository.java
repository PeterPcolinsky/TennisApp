package sk.peter.tenis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.peter.tenis.entity.PlayerEntity;

import java.util.Optional;

/**
 * Repository for working with player entities.
 *
 * <p>Provides basic CRUD operations inherited from {@link JpaRepository}
 * and additional query methods for player lookup.</p>
 */
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    /**
     * Finds a player by name ignoring case sensitivity.
     *
     * @param name player name
     * @return optional containing player if found
     */
    Optional<PlayerEntity> findByNameIgnoreCase(String name);

    /**
     * Checks whether a player with the given name exists (case-insensitive).
     *
     * @param name player name
     * @return {@code true} if player exists
     */
    boolean existsByNameIgnoreCase(String name);
}