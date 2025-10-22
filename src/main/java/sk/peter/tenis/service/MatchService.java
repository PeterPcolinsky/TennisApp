package sk.peter.tenis.service;

import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.MatchDto;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.dto.MatchUpdateDto;
import sk.peter.tenis.exception.NotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
     * Vytvorí zápas z DTO, skontroluje existenciu hráčov podľa mena
     * (case-insensitive) a uloží do CSV cez CsvService.saveMatches(..).
     * Duplicitám sa snažíme zabrániť rovnakou logikou ako v CsvService.
     */
    public Match createFromDto(MatchDto dto) {
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
                // nechajme túto výnimku PREJSŤ von – zachytí ju ApiExceptionHandler (400)
                throw new IllegalArgumentException("Player(s) not found");
            }

            // 3) zostav objekt Match
            LocalDate date = LocalDate.parse(dto.getDate()); // YYYY-MM-DD
            Match m = new Match(a, b, dto.getScore(), date);

            // 4) kontrola duplicity
            if (matchExists(matches, m)) {
                return m; // už existuje – nerobíme duplicitný zápis
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