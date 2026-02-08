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

    /**
     * Načíta všetky zápasy z CSV.
     * Použije CsvService.loadPlayers(..) a CsvService.loadMatches(..),
     * aby sa zachovalo rovnaké správanie/validácie ako v konzolovej verzii.
     */
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

    /**
     * Performs common business validation for a tennis match.
     * Used in both CSV and JPA modes.
     *
     * @param dto match input data
     * @throws IllegalArgumentException if validation rules are violated
     */
    public void validateMatchBusinessRules(MatchDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Zápas nesmie byť prázdny.");
        }

        String aName = dto.getPlayerA() != null ? dto.getPlayerA().trim() : "";
        String bName = dto.getPlayerB() != null ? dto.getPlayerB().trim() : "";

        if (aName.isEmpty() || bName.isEmpty()) {
            throw new IllegalArgumentException("Meno hráča A aj B je povinné.");
        }

        if (aName.equalsIgnoreCase(bName)) {
            throw new IllegalArgumentException("Hráč A a hráč B musia byť rozdielni.");
        }

        validateScore(dto.getScore());
    }

    /**
     * Validácia skóre – základné tenisové pravidlá pre jednotlivé sety.
     */
    private void validateScore(String rawScore) {
        if (rawScore == null) {
            throw new IllegalArgumentException("Skóre je povinné.");
        }

        String score = rawScore.trim();
        if (score.isEmpty()) {
            throw new IllegalArgumentException("Skóre je povinné.");
        }

        String[] sets = score.split(",");
        for (String part : sets) {
            String s = part.trim();
            String[] games = s.split(":");
            if (games.length != 2) {
                throw new IllegalArgumentException("Neplatný formát skóre. Použi napr. \"6:4\" alebo \"6:4, 7:6\".");
            }

            int gA;
            int gB;
            try {
                gA = Integer.parseInt(games[0].trim());
                gB = Integer.parseInt(games[1].trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Skóre musí obsahovať len čísla (napr. \"6:4\").");
            }

            if (gA == gB) {
                throw new IllegalArgumentException("Set nemôže skončiť remízou (napr. 6:6).");
            }

            int max = Math.max(gA, gB);
            int min = Math.min(gA, gB);

            if (max < 6) {
                throw new IllegalArgumentException("Víťaz setu musí mať aspoň 6 gemov.");
            }

            if (max == 6 && max - min < 2) {
                throw new IllegalArgumentException("Pri 6 gemoch musí byť rozdiel aspoň 2 (napr. 6:4).");
            }

            if (max == 7 && min < 5) {
                throw new IllegalArgumentException("Set 7:x je možný len ako 7:5 alebo 7:6.");
            }

            if (max > 7) {
                throw new IllegalArgumentException("Počet gemov v sete je príliš vysoký.");
            }
        }
    }

    /**
     * Creates a new match from DTO, validates business rules and persists it to CSV.
     * <p>
     * Players are resolved by name (case-insensitive). If an identical match already
     * exists, it is not stored again.
     *
     * @param dto match input data
     * @return created match
     * @throws IllegalArgumentException if players are not found or validation fails
     */
    public Match createFromDto(MatchDto dto) {
        // 🔥 spoločná biznis validácia
        validateMatchBusinessRules(dto);

        List<Player> players = new ArrayList<>();
        List<Match> matches = new ArrayList<>();

        try {
            // 1) načítaj existujúcich hráčov a zápasy
            CsvService.loadPlayers(players);
            CsvService.loadMatches(matches, players);

            // 2) nájdi hráčov podľa presného mena (case-insensitive)
            Player a = findPlayerByExactName(players, dto.getPlayerA());
            Player b = findPlayerByExactName(players, dto.getPlayerB());
            if (a == null || b == null) {
                // ⚠️ Toto musí ostať výnimka – test očakáva BAD_REQUEST
                throw new IllegalArgumentException("Player(s) not found");
            }

            // 3) zostav objekt Match
            LocalDate date = LocalDate.parse(dto.getDate()); // YYYY-MM-DD
            Match m = new Match(a, b, dto.getScore(), date);

            // 4) kontrola duplicity
            if (matchExists(matches, m)) {
                // ⚠️ Ak už zápas existuje, len ho vráť, ale NEulož znova
                return m;
            }

            // 5) ulož
            matches.add(m);
            CsvService.saveMatches(matches);
            return m;

        } catch (IllegalArgumentException iae) {
            // Toto necháme prejsť ďalej do ApiExceptionHandler → 400 + detailná správa
            throw iae;
        } catch (Exception e) {
            // Iné technické chyby (I/O, parse…) nechajme na generickú hlášku
            return null;
        }
    }

    // ===== pomocné metódy (lokálne, zrkadlia logiku z CsvService) =====

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

    /**
     * Updates an existing match identified by players, score and date.
     *
     * @param playerA name of player A
     * @param playerB name of player B
     * @param date    original match date
     * @param score   original match score
     * @param dto     new match values
     * @return updated match
     * @throws NotFoundException if the match does not exist
     */
    public Match update(String playerA, String playerB, String date, String score, MatchUpdateDto dto) {
        List<Player> players = new ArrayList<>();
        List<Match> matches = new ArrayList<>();
        try {
            // načítaj dáta
            CsvService.loadPlayers(players);
            CsvService.loadMatches(matches, players);

            // nájdi existujúci zápas podľa A,B,score,date (mena case-insensitive)
            Match existing = findMatch(matches, playerA, playerB, score, date);
            if (existing == null) {
                throw new NotFoundException("Match not found");
            }

            // nové hodnoty
            String newScore = (dto.getNewScore() != null && !dto.getNewScore().isBlank())
                    ? dto.getNewScore().trim()
                    : existing.getScore();

            LocalDate newDate = (dto.getNewDate() != null && !dto.getNewDate().isBlank())
                    ? LocalDate.parse(dto.getNewDate().trim())
                    : existing.getDate();

            // vytvor aktualizovaný objekt
            Match updated = new Match(existing.getPlayerA(), existing.getPlayerB(), newScore, newDate);

            // nahraď v kolekcii a ulož
            int idx = matches.indexOf(existing);
            matches.set(idx, updated);
            CsvService.saveMatches(matches);

            return updated;
        } catch (NotFoundException nf) {
            throw nf;
        } catch (Exception e) {
            // jednoduché spracovanie chýb v CSV fáze
            return null;
        }
    }

    /**
     * Deletes an existing match identified by players, score and date.
     *
     * @param playerA name of player A
     * @param playerB name of player B
     * @param date    match date
     * @param score   match score
     * @throws NotFoundException if the match does not exist
     */
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
            throw new RuntimeException("Unable to delete match");
        }
    }

}