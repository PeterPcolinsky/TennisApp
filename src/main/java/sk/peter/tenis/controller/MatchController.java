package sk.peter.tenis.controller;

import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.dto.MatchDto;
import sk.peter.tenis.dto.MatchResponseDto;
import sk.peter.tenis.dto.MatchUpdateDto;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.service.MatchService;
import sk.peter.tenis.service.jpa.MatchJpaService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for managing tennis matches.
 *
 * Supports two modes:
 * - CSV mode (default)
 * - JPA mode (when "mysql" profile is active)
 */
@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService csvService;
    private final MatchJpaService jpaService;
    private final Environment env;

    public MatchController(MatchService csvService,
                           MatchJpaService jpaService,
                           Environment env) {
        this.csvService = csvService;
        this.jpaService = jpaService;
        this.env = env;
    }

    private boolean isJpaActive() {
        return Arrays.stream(env.getActiveProfiles())
                .anyMatch(p -> p.equalsIgnoreCase("mysql"));
    }

    private MatchResponseDto toDto(MatchEntity match) {
        return new MatchResponseDto(
                match.getId(),
                match.getPlayerA().getName(),
                match.getPlayerB().getName(),
                match.getResult(),
                match.getDate()
        );
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @GetMapping
    public ResponseEntity<?> getAllMatches() {
        if (isJpaActive()) {
            return ResponseEntity.ok(
                    jpaService.findAll()
                            .stream()
                            .map(this::toDto)
                            .toList()
            );
        }
        return ResponseEntity.ok(csvService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createMatch(@RequestBody @Valid MatchDto matchDto) {

        if (isJpaActive()) {

            csvService.validateMatchBusinessRules(matchDto);

            MatchEntity saved = jpaService.save(matchDto);

            if (saved == null) {
                return badRequest("Player(s) not found");
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toDto(saved));
        }

        try {
            var created = csvService.createFromDto(matchDto);

            if (created == null ||
                    created.getPlayerA() == null ||
                    created.getPlayerB() == null) {
                return badRequest("Player(s) not found");
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(created);

        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception ex) {
            return badRequest("CSV operation failed");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatch(@PathVariable Long id,
                                         @RequestBody @Valid MatchUpdateDto dto) {

        if (isJpaActive()) {
            try {
                MatchEntity updated = jpaService.update(id, dto);
                return ResponseEntity.ok(toDto(updated));
            } catch (RuntimeException e) {
                return badRequest("Match not found");
            }
        }

        return badRequest("Update by ID not supported in CSV mode");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatchById(@PathVariable Long id) {

        if (isJpaActive()) {
            try {
                jpaService.deleteById(id);
                return ResponseEntity.noContent().build();
            } catch (RuntimeException e) {
                return badRequest("Match not found");
            }
        }

        return badRequest("Delete by ID not supported in CSV mode");
    }
}