package sk.peter.tenis.controller;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.dto.MatchDto;
import sk.peter.tenis.dto.MatchUpdateDto;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.service.MatchService;
import sk.peter.tenis.service.jpa.MatchJpaService;
import sk.peter.tenis.service.jpa.PlayerJpaService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService csvService;
    private final MatchJpaService jpaService;
    private final PlayerJpaService playerJpaService;
    private final Environment env;

    public MatchController(MatchService csvService,
                           MatchJpaService jpaService,
                           PlayerJpaService playerJpaService,
                           Environment env) {
        this.csvService = csvService;
        this.jpaService = jpaService;
        this.playerJpaService = playerJpaService;
        this.env = env;
    }

    private boolean isJpaActive() {
        // ⚠️ opravené – kontrolujeme aj "mysql"
        return Arrays.stream(env.getActiveProfiles())
                .anyMatch(p -> p.equalsIgnoreCase("mysql") || p.equalsIgnoreCase("h2"));
    }

    // -------------------- GET ALL --------------------
    @GetMapping
    public List<?> getAllMatches() {
        if (isJpaActive()) {
            return jpaService.findAll();
        }
        return csvService.findAll();
    }

    // -------------------- POST --------------------
    @PostMapping
    public Object createMatch(@RequestBody MatchDto matchDto) {
        if (isJpaActive()) {
            Player playerA = new Player(matchDto.getPlayerA(), 0, null);
            Player playerB = new Player(matchDto.getPlayerB(), 0, null);
            Match match = new Match(playerA, playerB, matchDto.getScore(), LocalDate.parse(matchDto.getDate()));

            jpaService.save(match);
            return match;
        }
        return csvService.createFromDto(matchDto);
    }

    // -------------------- PUT podľa ID (hlavná úprava) --------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatch(@PathVariable Long id, @RequestBody MatchUpdateDto dto) {
        if (isJpaActive()) {
            try {
                Match updated = jpaService.update(id, dto);
                return ResponseEntity.ok(updated);
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().body("Update by ID is supported only in JPA mode.");
        }
    }

    // -------------------- DELETE podľa ID --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatchById(@PathVariable Long id) {
        if (isJpaActive()) {
            jpaService.deleteById(id);
            return ResponseEntity.ok("🗑️ Match with ID " + id + " deleted.");
        } else {
            return ResponseEntity.badRequest().body("Delete by ID is supported only in JPA mode.");
        }
    }
}