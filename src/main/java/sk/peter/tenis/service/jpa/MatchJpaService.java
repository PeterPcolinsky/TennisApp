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

    // ---------- CREATE (DTO - používa controller) ----------
    @Transactional
    public MatchEntity save(MatchDto dto) {

        var playerAEntity = playerRepository.findByNameIgnoreCase(dto.getPlayerA());
        var playerBEntity = playerRepository.findByNameIgnoreCase(dto.getPlayerB());

        if (playerAEntity.isEmpty() || playerBEntity.isEmpty()) {
            return null;
        }

        MatchEntity entity = new MatchEntity();
        entity.setPlayerA(playerAEntity.get());
        entity.setPlayerB(playerBEntity.get());
        entity.setResult(dto.getScore());
        entity.setDate(LocalDate.parse(dto.getDate()));

        return matchRepository.save(entity);
    }

    // ---------- CREATE (Match - používa DataSeeder) ----------
    @Transactional
    public MatchEntity save(Match match) {

        var playerAEntity = playerRepository.findByNameIgnoreCase(match.getPlayerA().getName());
        var playerBEntity = playerRepository.findByNameIgnoreCase(match.getPlayerB().getName());

        if (playerAEntity.isEmpty() || playerBEntity.isEmpty()) {
            return null;
        }

        MatchEntity entity = new MatchEntity();
        entity.setPlayerA(playerAEntity.get());
        entity.setPlayerB(playerBEntity.get());
        entity.setResult(match.getScore());
        entity.setDate(match.getDate());

        return matchRepository.save(entity);
    }

    // ---------- UPDATE ----------
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
    public void deleteById(Long id) {

        if (!matchRepository.existsById(id)) {
            throw new RuntimeException("Match ID not found");
        }

        matchRepository.deleteById(id);
    }

    // ---------- FIND ALL ----------
    public List<MatchEntity> findAll() {
        return matchRepository.findAll();
    }

    public boolean existsById(Long id) {
        return matchRepository.existsById(id);
    }

    public long count() {
        return matchRepository.count();
    }
}