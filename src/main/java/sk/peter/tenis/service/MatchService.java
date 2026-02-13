package sk.peter.tenis.service;

import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.MatchDto;
import sk.peter.tenis.dto.MatchUpdateDto;
import sk.peter.tenis.exception.NotFoundException;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing tennis matches.
 * <p>
 * Handles loading, validation, creation, update and deletion of matches
 * in CSV-based mode, keeping the same business rules as the console version.
 */
@Service
public class MatchService {

    public List<Match> findAll() {
        List<Player> players = new ArrayList<>();
        List<Match> matches = new ArrayList<>();
        try {
            CsvService.loadPlayers(players);
            CsvService.loadMatches(matches, players);
        } catch (Exception ignored) {
        }
        return matches;
    }

    public void validateMatchBusinessRules(MatchDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Match must not be null.");
        }

        String aName = dto.getPlayerA() != null ? dto.getPlayerA().trim() : "";
        String bName = dto.getPlayerB() != null ? dto.getPlayerB().trim() : "";

        if (aName.isEmpty() || bName.isEmpty()) {
            throw new IllegalArgumentException("Both player A and player B names are required.");
        }

        if (aName.equalsIgnoreCase(bName)) {
            throw new IllegalArgumentException("Player A and player B must be different.");
        }

        validateScore(dto.getScore());
    }

    private void validateScore(String rawScore) {
        if (rawScore == null) {
            throw new IllegalArgumentException("Score is required.");
        }

        String score = rawScore.trim();
        if (score.isEmpty()) {
            throw new IllegalArgumentException("Score is required.");
        }

        String[] sets = score.split(",");
        for (String part : sets) {
            String s = part.trim();
            String[] games = s.split(":");
            if (games.length != 2) {
                throw new IllegalArgumentException("Invalid score format. Use e.g. \"6:4\" or \"6:4, 7:6\".");
            }

            int gA;
            int gB;
            try {
                gA = Integer.parseInt(games[0].trim());
                gB = Integer.parseInt(games[1].trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Score must contain numbers only (e.g. \"6:4\").");
            }

            if (gA == gB) {
                throw new IllegalArgumentException("A set cannot end in a draw (e.g. 6:6).");
            }

            int max = Math.max(gA, gB);
            int min = Math.min(gA, gB);

            if (max < 6) {
                throw new IllegalArgumentException("Set winner must have at least 6 games.");
            }

            if (max == 6 && max - min < 2) {
                throw new IllegalArgumentException("At 6 games, the difference must be at least 2 (e.g. 6:4).");
            }

            if (max == 7 && min < 5) {
                throw new IllegalArgumentException("A 7:x set is only allowed as 7:5 or 7:6.");
            }

            if (max > 7) {
                throw new IllegalArgumentException("Too many games in a set.");
            }
        }
    }

    public Match createFromDto(MatchDto dto) {
        validateMatchBusinessRules(dto);

        List<Player> players = new ArrayList<>();
        List<Match> matches = new ArrayList<>();

        try {
            CsvService.loadPlayers(players);
            CsvService.loadMatches(matches, players);

            Player a = findPlayerByExactName(players, dto.getPlayerA());
            Player b = findPlayerByExactName(players, dto.getPlayerB());
            if (a == null || b == null) {
                throw new IllegalArgumentException("Player(s) not found");
            }

            LocalDate date = LocalDate.parse(dto.getDate());
            Match m = new Match(a, b, dto.getScore(), date);

            if (matchExists(matches, m)) {
                return m;
            }

            matches.add(m);
            CsvService.saveMatches(matches);
            return m;

        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create match", e);
        }
    }

    private Player findPlayerByExactName(List<Player> players, String name) {
        if (name == null) return null;
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name.trim())) return p;
        }
        return null;
    }

    private boolean matchExists(List<Match> list, Match candidate) {
        for (Match m : list) {
            boolean sameOrder =
                    m.getPlayerA().getName().equalsIgnoreCase(candidate.getPlayerA().getName()) &&
                            m.getPlayerB().getName().equalsIgnoreCase(candidate.getPlayerB().getName());
            boolean swappedOrder =
                    m.getPlayerA().getName().equalsIgnoreCase(candidate.getPlayerB().getName()) &&
                            m.getPlayerB().getName().equalsIgnoreCase(candidate.getPlayerA().getName());

            if ((sameOrder || swappedOrder)
                    && m.getScore().equalsIgnoreCase(candidate.getScore())
                    && m.getDate().equals(candidate.getDate())) {
                return true;
            }
        }
        return false;
    }

    private Match findMatch(List<Match> list, String aName, String bName, String score, String dateStr) {
        if (aName == null || bName == null || score == null || dateStr == null) return null;
        LocalDate d;
        try {
            d = LocalDate.parse(dateStr.trim());
        } catch (Exception e) {
            return null;
        }

        for (Match m : list) {
            boolean sameOrder =
                    m.getPlayerA().getName().equalsIgnoreCase(aName.trim()) &&
                            m.getPlayerB().getName().equalsIgnoreCase(bName.trim());
            boolean swappedOrder =
                    m.getPlayerA().getName().equalsIgnoreCase(bName.trim()) &&
                            m.getPlayerB().getName().equalsIgnoreCase(aName.trim());

            if ((sameOrder || swappedOrder)
                    && m.getScore().equalsIgnoreCase(score.trim())
                    && m.getDate().equals(d)) {
                return m;
            }
        }
        return null;
    }

    public Match update(String playerA, String playerB, String date, String score, MatchUpdateDto dto) {
        List<Player> players = new ArrayList<>();
        List<Match> matches = new ArrayList<>();
        try {
            CsvService.loadPlayers(players);
            CsvService.loadMatches(matches, players);

            Match existing = findMatch(matches, playerA, playerB, score, date);
            if (existing == null) {
                throw new NotFoundException("Match not found");
            }

            String newScore = (dto.getNewScore() != null && !dto.getNewScore().isBlank())
                    ? dto.getNewScore().trim()
                    : existing.getScore();

            LocalDate newDate = (dto.getNewDate() != null && !dto.getNewDate().isBlank())
                    ? LocalDate.parse(dto.getNewDate().trim())
                    : existing.getDate();

            Match updated = new Match(existing.getPlayerA(), existing.getPlayerB(), newScore, newDate);

            int idx = matches.indexOf(existing);
            matches.set(idx, updated);
            CsvService.saveMatches(matches);

            return updated;
        } catch (NotFoundException nf) {
            throw nf;
        } catch (Exception e) {
            throw new RuntimeException("Unable to update match", e);
        }
    }

    public void delete(String playerA, String playerB, String date, String score) {
        List<Player> players = new ArrayList<>();
        List<Match> matches = new ArrayList<>();
        try {
            CsvService.loadPlayers(players);
            CsvService.loadMatches(matches, players);

            Match existing = findMatch(matches, playerA, playerB, score, date);
            if (existing == null) {
                throw new NotFoundException("Match not found");
            }

            matches.remove(existing);
            CsvService.saveMatches(matches);
        } catch (NotFoundException nf) {
            throw nf;
        } catch (Exception e) {
            throw new RuntimeException("Unable to delete match", e);
        }
    }
}