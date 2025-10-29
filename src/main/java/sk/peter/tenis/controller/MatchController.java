package sk.peter.tenis.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.dto.MatchDto;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.service.MatchService;
import sk.peter.tenis.service.jpa.MatchJpaService;
import sk.peter.tenis.service.jpa.PlayerJpaService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Controller pre zápasy (CSV + JPA režim).
 */
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
        return Arrays.stream(env.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("h2"));
    }

    // ---------------- CRUD ----------------

    @GetMapping
    public List<?> getAllMatches() {
        if (isJpaActive()) {
            return jpaService.findAll();
        }
        return csvService.findAll();
    }

    /**
     * Vytvorí nový zápas.
     * Ak beží JPA profil (h2), hráčov vyhľadá priamo v databáze podľa mena.
     */
    @PostMapping
    public Object createMatch(@RequestBody MatchDto dto) {
        if (isJpaActive()) {
            // nájdeme hráčov podľa mena (case-insensitive)
            PlayerEntity playerA = playerJpaService.findAll().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(dto.getPlayerA()))
                    .findFirst()
                    .orElse(null);

            PlayerEntity playerB = playerJpaService.findAll().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(dto.getPlayerB()))
                    .findFirst()
                    .orElse(null);

            if (playerA == null || playerB == null) {
                throw new IllegalArgumentException("One or both players not found in database");
            }

            MatchEntity entity = new MatchEntity(
                    playerA,
                    playerB,
                    dto.getScore(),
                    dto.getDate() != null ? LocalDate.parse(dto.getDate()) : null
            );

            return jpaService.save(entity);
        }

        return csvService.createFromDto(dto);
    }

    /**
     * Vymaže zápas podľa hráčov, dátumu a výsledku.
     */
    @DeleteMapping("/{playerA}/{playerB}/{date}/{score}")
    public void deleteMatch(@PathVariable String playerA,
                            @PathVariable String playerB,
                            @PathVariable String date,
                            @PathVariable String score) {
        if (isJpaActive()) {
            jpaService.findAll().stream()
                    .filter(m -> m.getResult().equalsIgnoreCase(score))
                    .filter(m -> m.getDate().toString().equalsIgnoreCase(date))
                    .findFirst()
                    .ifPresent(m -> jpaService.deleteById(m.getId()));
        } else {
            csvService.delete(playerA, playerB, date, score);
        }
    }
}