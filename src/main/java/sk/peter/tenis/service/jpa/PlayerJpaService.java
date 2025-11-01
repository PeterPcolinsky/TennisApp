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
import java.util.stream.Collectors;

@Service
@Profile({"h2", "mysql"})
public class PlayerJpaService {

    private final PlayerRepository playerRepository;

    public PlayerJpaService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // ---------------- SAVE ----------------
    public void save(Player player) {
        Optional<PlayerEntity> existing = playerRepository.findByNameIgnoreCase(player.getName());

        if (existing.isPresent()) {
            PlayerEntity entity = existing.get();
            entity.setAge(player.getAge());
            entity.setType(player.getType());
            playerRepository.save(entity);
            System.out.println("🔁 Aktualizovaný hráč: " + player.getName());
        } else {
            PlayerEntity newEntity = new PlayerEntity();
            newEntity.setName(player.getName());
            newEntity.setAge(player.getAge());
            newEntity.setType(player.getType());
            playerRepository.save(newEntity);
            System.out.println("➕ Pridaný nový hráč: " + player.getName());
        }
    }

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
    public List<PlayerEntity> findAll() {
        return playerRepository.findAll();
    }

    // ---------------- FIND BY ID ----------------
    public Optional<PlayerEntity> findById(Long id) {
        return playerRepository.findById(id);
    }

    // ---------------- DELETE BY ID ----------------
    public void deleteById(Long id) {
        playerRepository.deleteById(id);
    }

    // ---------------- DELETE BY NAME ----------------
    public void deleteByName(String name) {
        playerRepository.findByNameIgnoreCase(name)
                .ifPresent(player -> playerRepository.deleteById(player.getId()));
    }
}