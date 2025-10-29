package sk.peter.tenis.service.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.repository.PlayerRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA verzia služby pre správu hráčov.
 * Používa sa len v profile "h2".
 */
@Service
@Profile("h2")
public class PlayerJpaService {

    private final PlayerRepository playerRepository;

    public PlayerJpaService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Vráti všetkých hráčov z databázy.
     */
    public List<PlayerEntity> findAll() {
        return playerRepository.findAll();
    }

    /**
     * Nájde hráča podľa ID.
     */
    public Optional<PlayerEntity> findById(Long id) {
        return playerRepository.findById(id);
    }

    /**
     * Uloží alebo aktualizuje hráča.
     */
    public PlayerEntity save(PlayerEntity player) {
        return playerRepository.save(player);
    }

    /**
     * Vymaže hráča podľa ID.
     */
    public void deleteById(Long id) {
        playerRepository.deleteById(id);
    }
}