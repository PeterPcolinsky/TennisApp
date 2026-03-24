package sk.peter.tenis.service.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.PlayerDto;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.repository.PlayerRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA service for player management.
 *
 * <p>This service handles basic player operations such as saving,
 * updating, deleting and reading player records from the database.</p>
 */
@Service
@Profile({"h2", "mysql"})
public class PlayerJpaService {

    private final PlayerRepository playerRepository;

    /**
     * Creates a new player service using the given repository.
     *
     * @param playerRepository repository for player entities
     */
    public PlayerJpaService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // ---------------- SAVE ----------------

    /**
     * Saves a new player into the database.
     *
     * <p>Before saving, the method checks whether a player with the same
     * name already exists, ignoring letter case.</p>
     *
     * @param player player domain object to save
     * @throws IllegalArgumentException if player with the same name already exists
     */
    public void save(Player player) {
        boolean exists = playerRepository.existsByNameIgnoreCase(player.getName());
        if (exists) {
            throw new IllegalArgumentException(
                    "Hráč s týmto menom už existuje. Zadaj prosím celé meno (meno + priezvisko), aby sme ich vedeli odlíšiť."
            );
        }

        PlayerEntity newEntity = new PlayerEntity();
        newEntity.setName(player.getName());
        newEntity.setAge(player.getAge());
        newEntity.setType(player.getType());
        playerRepository.save(newEntity);
        System.out.println("➕ Pridaný nový hráč: " + player.getName());
    }

    /**
     * Updates an existing player identified by name.
     *
     * <p>The player's age and type are updated based on the provided DTO.</p>
     *
     * @param name current player name
     * @param dto DTO containing new player values
     * @return updated player as domain model
     * @throws RuntimeException if player is not found
     */
    public Player update(String name, PlayerDto dto) {
        Optional<PlayerEntity> optionalEntity = playerRepository.findByNameIgnoreCase(name);

        if (optionalEntity.isEmpty()) {
            throw new RuntimeException("Player not found: " + name);
        }

        PlayerEntity entity = optionalEntity.get();
        entity.setAge(dto.getAge());
        entity.setType(PlayerType.fromInput(dto.getType()));

        playerRepository.save(entity);

        System.out.println("🔁 Aktualizovaný hráč: " + entity.getName());
        return new Player(entity.getName(), entity.getAge(), entity.getType());
    }

    // ---------------- FIND ALL ----------------

    /**
     * Returns all stored players.
     *
     * @return list of all player entities
     */
    public List<PlayerEntity> findAll() {
        return playerRepository.findAll();
    }

    // ---------------- FIND BY ID ----------------

    /**
     * Finds a player by database ID.
     *
     * @param id player ID
     * @return optional with player entity if found
     */
    public Optional<PlayerEntity> findById(Long id) {
        return playerRepository.findById(id);
    }

    // ---------------- DELETE BY ID ----------------

    /**
     * Deletes a player by database ID.
     *
     * @param id player ID
     */
    public void deleteById(Long id) {
        playerRepository.deleteById(id);
    }

    // ---------------- DELETE BY NAME ----------------

    /**
     * Deletes a player by name if such player exists.
     *
     * @param name player name
     */
    public void deleteByName(String name) {
        playerRepository.findByNameIgnoreCase(name)
                .ifPresent(player -> playerRepository.deleteById(player.getId()));
    }

    /**
     * Returns the total number of stored players.
     *
     * @return number of players
     */
    public long count() {
        return playerRepository.count();
    }
}