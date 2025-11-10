package sk.peter.tenis.controller;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.dto.PlayerDto;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.service.PlayerService;
import sk.peter.tenis.service.jpa.PlayerJpaService;

import java.util.Arrays;
import java.util.List;

/**
 * Controller pre správu hráčov (CSV + JPA režim).
 */
@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService csvService;
    private final PlayerJpaService jpaService;
    private final Environment env;

    public PlayerController(PlayerService csvService,
                            PlayerJpaService jpaService,
                            Environment env) {
        this.csvService = csvService;
        this.jpaService = jpaService;
        this.env = env;
    }

    private boolean isJpaActive() {
        return Arrays.stream(env.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("h2") || p.equalsIgnoreCase("mysql"));
    }

    // ---------------- CRUD ----------------

    @GetMapping
    public List<?> getAllPlayers() {
        if (isJpaActive()) {
            return jpaService.findAll();
        }
        return csvService.findAll();
    }

    @GetMapping("/{id}")
    public Object getPlayer(@PathVariable Long id) {
        if (isJpaActive()) {
            return jpaService.findById(id).orElse(null);
        }
        return csvService.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(String.valueOf(id)))
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    public ResponseEntity<Object> createPlayer(@RequestBody PlayerDto playerDto) {
        Object savedPlayer;

        if (isJpaActive()) {
            // typ hráča
            sk.peter.tenis.model.PlayerType type = sk.peter.tenis.model.PlayerType.fromInput(playerDto.getType());
            if (type == null) {
                try {
                    type = sk.peter.tenis.model.PlayerType.valueOf(playerDto.getType().trim().toUpperCase());
                } catch (Exception ignored) {
                    type = sk.peter.tenis.model.PlayerType.AMATER;
                }
            }

            // vytvor model Player
            sk.peter.tenis.model.Player player = new sk.peter.tenis.model.Player(
                    playerDto.getName(),
                    playerDto.getAge(),
                    type
            );

            // ulož cez JPA service
            jpaService.save(player);
            savedPlayer = player;
        } else {
            // CSV režim
            savedPlayer = csvService.createFromDto(playerDto);
        }

        // ✅ REST štandard: 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer);
    }

    @PutMapping("/{name}")
    public ResponseEntity<?> updatePlayer(@PathVariable String name, @RequestBody PlayerDto dto) {
        if (isJpaActive()) {
            Player updated = jpaService.update(name, dto);
            return ResponseEntity.ok(updated);
        } else {
            Player updated = csvService.update(name, dto);
            return ResponseEntity.ok(updated);
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deletePlayer(@PathVariable String name) {
        if (isJpaActive()) {
            // JPA režim – mazanie podľa mena
            jpaService.findAll().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .ifPresent(p -> jpaService.deleteById(p.getId()));
        } else {
            csvService.delete(name);
        }

        // ✅ REST štandard: 204 – No Content
        return ResponseEntity.noContent().build();
    }
}