package sk.peter.tenis.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.dto.MatchDto;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.service.MatchService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sk.peter.tenis.dto.MatchUpdateDto;
import sk.peter.tenis.exception.NotFoundException;

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

    @PutMapping
    public Match update(
            @RequestParam String playerA,
            @RequestParam String playerB,
            @RequestParam String score,
            @RequestParam String date,
            @RequestBody @Valid MatchUpdateDto dto
    ) {
        Match m = matches.update(playerA, playerB, date, score, dto);
        if (m == null) {
            throw new IllegalArgumentException("Unable to update match");
        }
        return m;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestParam String playerA,
            @RequestParam String playerB,
            @RequestParam String score,
            @RequestParam String date
    ) {
        matches.delete(playerA, playerB, date, score);
    }

    @GetMapping("/filter")
    public List<Match> filter(
            @RequestParam(required = false) String player,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        List<Match> all = matches.findAll();

        java.time.LocalDate fromDate = parseDate(from);
        java.time.LocalDate toDate = parseDate(to);
        String playerLc = player == null ? null : player.trim().toLowerCase();

        return all.stream()
                .filter(m -> {
                    // filter player
                    if (playerLc != null && !playerLc.isBlank()) {
                        String a = m.getPlayerA().getName().toLowerCase();
                        String b = m.getPlayerB().getName().toLowerCase();
                        if (!a.equals(playerLc) && !b.equals(playerLc)) return false;
                    }
                    // filter from (inclusive)
                    if (fromDate != null && m.getDate().isBefore(fromDate)) return false;
                    // filter to (inclusive)
                    if (toDate != null && m.getDate().isAfter(toDate)) return false;
                    return true;
                })
                .toList();
    }

    private java.time.LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return java.time.LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        } // nevalidný dátum ignorujeme
    }

}