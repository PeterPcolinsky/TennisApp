package sk.peter.tenis.service.jpa;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.MatchUpdateDto;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;
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

    // ---------- CREATE ----------
    @Transactional
    public MatchEntity save(Match match) {
        Player playerA = match.getPlayerA();
        Player playerB = match.getPlayerB();

        // Skús nájsť hráčov podľa mena
        var playerAEntity = playerRepository.findByNameIgnoreCase(playerA.getName());
        var playerBEntity = playerRepository.findByNameIgnoreCase(playerB.getName());

        // Ak niektorý hráč chýba -> vráť null (test očakáva 400 pre chýbajúceho hráča)
        if (playerAEntity.isEmpty() || playerBEntity.isEmpty()) {
            return null;
        }

        // Vytvor zápas
        MatchEntity entity = new MatchEntity();
        entity.setPlayerA(playerAEntity.get());
        entity.setPlayerB(playerBEntity.get());
        entity.setResult(match.getScore());
        entity.setDate(match.getDate());

        return matchRepository.save(entity);
    }

    // ---------- UPDATE ----------
    @Transactional
    public Match update(Long id, MatchUpdateDto dto) {
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

        var saved = matchRepository.save(existing);

        return new Match(
                new Player(saved.getPlayerA().getName(), 0, null),
                new Player(saved.getPlayerB().getName(), 0, null),
                saved.getResult(),
                saved.getDate()
        );
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

    // ---------- EXISTS ----------
    public boolean existsById(Long id) {
        return matchRepository.existsById(id);
    }
}