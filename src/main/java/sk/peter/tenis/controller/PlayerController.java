package sk.peter.tenis.controller;

import org.springframework.core.env.Environment;
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
        return Arrays.stream(env.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("h2"));
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
    public Object createPlayer(@RequestBody PlayerDto playerDto) {
        if (isJpaActive()) {
            // pretypovanie String -> PlayerType
            sk.peter.tenis.model.PlayerType type =
                    sk.peter.tenis.model.PlayerType.fromInput(playerDto.getType());

            if (type == null) {
                try {
                    type = sk.peter.tenis.model.PlayerType.valueOf(playerDto.getType().trim().toUpperCase());
                } catch (Exception ignored) {
                    type = sk.peter.tenis.model.PlayerType.AMATER;
                }
            }

            PlayerEntity entity = new PlayerEntity(
                    playerDto.getName(),
                    playerDto.getAge(),
                    type
            );
            return jpaService.save(entity);
        }
        return csvService.createFromDto(playerDto);
    }

    @DeleteMapping("/{name}")
    public void deletePlayer(@PathVariable String name) {
        if (isJpaActive()) {
            // Pre JPA režim by sme mohli mazať podľa ID, ale zachovajme názov
            jpaService.findAll().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .ifPresent(p -> jpaService.deleteById(p.getId()));
        } else {
            csvService.delete(name);
        }
    }
}