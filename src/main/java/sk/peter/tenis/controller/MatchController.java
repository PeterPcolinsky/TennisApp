package sk.peter.tenis.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.dto.MatchDto;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matches;

    public MatchController(MatchService matches) {
        this.matches = matches;
    }

    @GetMapping
    public List<Match> all() {
        return matches.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Match create(@Valid @RequestBody MatchDto dto) {
        Match m = matches.createFromDto(dto);
        if (m == null) {
            throw new IllegalArgumentException("Unable to create match (check players/score/date).");
        }
        return m;
    }
}