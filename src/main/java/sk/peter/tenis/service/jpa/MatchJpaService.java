package sk.peter.tenis.service.jpa;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.MatchDto;
import sk.peter.tenis.dto.MatchUpdateDto;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.repository.MatchRepository;
import sk.peter.tenis.repository.PlayerRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA service for tennis match management.
 *
 * <p>This service provides create, update, delete and read operations
 * for matches stored in the database.</p>
 */
@Service
@Profile({"h2", "mysql"})
public class MatchJpaService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    /**
     * Creates a new match service using JPA repositories.
     *
     * @param matchRepository repository for match entities
     * @param playerRepository repository for player entities
     */
    public MatchJpaService(MatchRepository matchRepository,
                           PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    // ---------- CREATE (DTO - používa controller) ----------

    /**
     * Saves a new match from controller DTO input.
     *
     * <p>If one of the referenced players does not exist,
     * the method throws an {@link IllegalArgumentException}.</p>
     *
     * @param dto match DTO from request
     * @return saved match entity
     * @throws IllegalArgumentException if one or both players were not found
     */
    @Transactional
    public MatchEntity save(MatchDto dto) {

        var playerAEntity = playerRepository.findByNameIgnoreCase(dto.getPlayerA());
        var playerBEntity = playerRepository.findByNameIgnoreCase(dto.getPlayerB());

        if (playerAEntity.isEmpty() || playerBEntity.isEmpty()) {
            throw new IllegalArgumentException("One or both players not found");
        }

        MatchEntity entity = new MatchEntity();
        entity.setPlayerA(playerAEntity.get());
        entity.setPlayerB(playerBEntity.get());
        entity.setResult(dto.getScore());
        entity.setDate(LocalDate.parse(dto.getDate()));

        return matchRepository.save(entity);
    }

    // ---------- CREATE (Match - používa DataSeeder) ----------

    /**
     * Saves a new match from domain model input.
     *
     * <p>This overload is mainly used by seeders when importing data.</p>
     *
     * @param match match domain object
     * @return saved match entity
     * @throws IllegalArgumentException if one or both players were not found
     */
    @Transactional
    public MatchEntity save(Match match) {

        var playerAEntity = playerRepository.findByNameIgnoreCase(match.getPlayerA().getName());
        var playerBEntity = playerRepository.findByNameIgnoreCase(match.getPlayerB().getName());

        if (playerAEntity.isEmpty() || playerBEntity.isEmpty()) {
            throw new IllegalArgumentException("One or both players not found");
        }

        MatchEntity entity = new MatchEntity();
        entity.setPlayerA(playerAEntity.get());
        entity.setPlayerB(playerBEntity.get());
        entity.setResult(match.getScore());
        entity.setDate(match.getDate());

        return matchRepository.save(entity);
    }

    // ---------- UPDATE ----------

    /**
     * Updates an existing match by its ID.
     *
     * <p>If a new score or date is not provided, the existing value is kept.</p>
     *
     * @param id match ID
     * @param dto DTO containing updated match values
     * @return updated match entity
     * @throws RuntimeException if the match does not exist
     */
    @Transactional
    public MatchEntity update(Long id, MatchUpdateDto dto) {

        var existing = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        String newScore = (dto.getNewScore() != null && !dto.getNewScore().isBlank())
                ? dto.getNewScore().trim()
                : existing.getResult();

        LocalDate newDate = (dto.getNewDate() != null && !dto.getNewDate().isBlank())
                ? LocalDate.parse(dto.getNewDate().trim())
                : existing.getDate();

        existing.setResult(newScore);
        existing.setDate(newDate);

        return matchRepository.save(existing);
    }

    // ---------- DELETE ----------

    /**
     * Deletes a match by its ID.
     *
     * @param id match ID
     * @throws RuntimeException if the match ID does not exist
     */
    public void deleteById(Long id) {

        if (!matchRepository.existsById(id)) {
            throw new RuntimeException("Match ID not found");
        }

        matchRepository.deleteById(id);
    }

    // ---------- FIND ALL ----------

    /**
     * Returns all stored matches.
     *
     * @return list of all match entities
     */
    public List<MatchEntity> findAll() {
        return matchRepository.findAll();
    }

    /**
     * Checks whether a match with the given ID exists.
     *
     * @param id match ID
     * @return {@code true} if the match exists, otherwise {@code false}
     */
    public boolean existsById(Long id) {
        return matchRepository.existsById(id);
    }

    /**
     * Returns the total number of stored matches.
     *
     * @return number of matches
     */
    public long count() {
        return matchRepository.count();
    }
}