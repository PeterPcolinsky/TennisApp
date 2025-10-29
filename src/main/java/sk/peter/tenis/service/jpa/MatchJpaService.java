package sk.peter.tenis.service.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.repository.MatchRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA verzia služby pre správu zápasov.
 * Používa sa len v profile "h2".
 */
@Service
@Profile("h2")
public class MatchJpaService {

    private final MatchRepository matchRepository;

    public MatchJpaService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    /**
     * Vráti všetky zápasy z databázy.
     */
    public List<MatchEntity> findAll() {
        return matchRepository.findAll();
    }

    /**
     * Nájde zápas podľa ID.
     */
    public Optional<MatchEntity> findById(Long id) {
        return matchRepository.findById(id);
    }

    /**
     * Uloží alebo aktualizuje zápas.
     */
    public MatchEntity save(MatchEntity match) {
        return matchRepository.save(match);
    }

    /**
     * Vymaže zápas podľa ID.
     */
    public void deleteById(Long id) {
        matchRepository.deleteById(id);
    }

    /**
     * Nájde zápasy, kde hrá daný hráč (ako A alebo B).
     */
    public List<MatchEntity> findByPlayerName(String name) {
        if (name == null || name.isBlank()) return List.of();

        return matchRepository.findAll().stream()
                .filter(m ->
                        m.getPlayerA().getName().equalsIgnoreCase(name.trim()) ||
                                m.getPlayerB().getName().equalsIgnoreCase(name.trim()))
                .toList();
    }
}