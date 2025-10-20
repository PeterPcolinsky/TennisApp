package sk.peter.tenis.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.service.PlayerService;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
}