package sk.peter.tenis.service.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.MatchUpdateDto;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.repository.MatchRepository;
import sk.peter.tenis.repository.PlayerRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Profile({"h2", "mysql"})
public class MatchJpaService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    public MatchJpaService(MatchRepository matchRepository,
                           PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    // -------------------- SAVE --------------------
    public void save(Match match) {
        Player playerA = match.getPlayerA();
        Player playerB = match.getPlayerB();

        Optional<PlayerEntity> playerAEntity = playerRepository.findByNameIgnoreCase(playerA.getName());
        Optional<PlayerEntity> playerBEntity = playerRepository.findByNameIgnoreCase(playerB.getName());

        if (playerAEntity.isEmpty() || playerBEntity.isEmpty()) {
            System.err.println("⚠️ Nepodarilo sa nájsť hráčov pre zápas: "
                    + playerA.getName() + " vs " + playerB.getName());
            return;
        }

        MatchEntity entity = new MatchEntity();
        entity.setPlayerA(playerAEntity.get());
        entity.setPlayerB(playerBEntity.get());
        entity.setResult(match.getScore());
        entity.setDate(match.getDate());

        matchRepository.save(entity);
        System.out.println("➕ Uložený zápas: " +
                playerA.getName() + " vs " + playerB.getName() + " (" + match.getScore() + ")");
    }

    // -------------------- UPDATE podľa ID --------------------
    public Match update(Long id, MatchUpdateDto dto) {
        Optional<MatchEntity> optional = matchRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Match not found with ID: " + id);
        }

        MatchEntity entity = optional.get();

        if (dto.getNewScore() != null && !dto.getNewScore().isBlank()) {
            entity.setResult(dto.getNewScore());
        }

        if (dto.getNewDate() != null && !dto.getNewDate().isBlank()) {
            entity.setDate(LocalDate.parse(dto.getNewDate()));
        }

        MatchEntity saved = matchRepository.save(entity);
        System.out.println("🔁 Aktualizovaný zápas: " +
                saved.getPlayerA().getName() + " vs " + saved.getPlayerB().getName());

        return new Match(
                new Player(saved.getPlayerA().getName(), saved.getPlayerA().getAge(), saved.getPlayerA().getType()),
                new Player(saved.getPlayerB().getName(), saved.getPlayerB().getAge(), saved.getPlayerB().getType()),
                saved.getResult(),
                saved.getDate()
        );
    }

    // -------------------- FIND ALL --------------------
    public List<MatchEntity> findAll() {
        return matchRepository.findAll();
    }

    // -------------------- FIND BY ID --------------------
    public Optional<MatchEntity> findById(Long id) {
        return matchRepository.findById(id);
    }

    // -------------------- DELETE BY ID --------------------
    public void deleteById(Long id) {
        if (matchRepository.existsById(id)) {
            matchRepository.deleteById(id);
            System.out.println("🗑️ Zmazaný zápas s ID: " + id);
        } else {
            System.out.println("⚠️ Zápas s ID " + id + " neexistuje.");
        }
    }

    // -------------------- SEARCH --------------------
    public List<MatchEntity> search(String name, java.time.LocalDate from, java.time.LocalDate to) {
        return matchRepository.search(name, from, to);
    }
}