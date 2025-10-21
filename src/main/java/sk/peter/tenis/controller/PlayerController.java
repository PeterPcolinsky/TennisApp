package sk.peter.tenis.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.service.PlayerService;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import sk.peter.tenis.dto.PlayerDto;
import sk.peter.tenis.model.Player;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService players;

    public PlayerController(PlayerService players) { this.players = players; }

    @GetMapping
    public List<Player> all() { return players.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Player create(@Valid @RequestBody PlayerDto dto) {
        return players.createFromDto(dto);
    }

    @PutMapping("/{name}")
    public Player update(@PathVariable String name, @Valid @RequestBody PlayerDto dto) {
        return players.update(name, dto);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String name) {
        players.delete(name);
    }
}