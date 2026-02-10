package sk.peter.tenis.service;

import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.model.PlayerType;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV I/O service for players and matches stored in {@code data/*.csv}.
 * <p>
 * Supported formats:
 * <ul>
 *   <li>Players: {@code "Meno;Vek;Typ"}</li>
 *   <li>Matches: {@code "HracA;HracB;Vysledok;Datum"}</li>
 * </ul>
 * This service is used in CSV mode to mirror the same business rules
 * as the console version of the application.
 */
public final class CsvService {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path PLAYERS_CSV = DATA_DIR.resolve("players.csv");
    private static final Path MATCHES_CSV = DATA_DIR.resolve("matches.csv");

    private CsvService() {
    }

    // ====================== PLAYERS ======================

    /**
     * Loads all players from {@code players.csv}.
     *
     * @return list of loaded players
     * @throws Exception if file I/O fails
     */
    public static List<Player> loadPlayersFromCsv() throws Exception {
        List<Player> players = new ArrayList<>();
        loadPlayers(players);
        return players;
    }

    /**
     * Loads players from {@code players.csv} into the provided target list.
     * Duplicate player names (case-insensitive) are ignored.
     *
     * @param target target list to be filled with players
     * @throws Exception if file I/O fails
     */
    public static void loadPlayers(List<Player> target) throws Exception {
        ensureDataDir();

        if (!Files.exists(PLAYERS_CSV)) {
            try (var w = Files.newBufferedWriter(PLAYERS_CSV, StandardCharsets.UTF_8)) {
                w.write("Meno;Vek;Typ");
                w.newLine();
            }
            return;
        }

        try (var reader = Files.newBufferedReader(PLAYERS_CSV, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                if (first) {
                    first = false;
                    if (line.toLowerCase().startsWith("meno;")) continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length < 3) continue;

                String name = parts[0].trim();
                String ageStr = parts[1].trim();
                String typeStr = parts[2].trim();

                int age;
                try {
                    age = Integer.parseInt(ageStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                PlayerType type = PlayerType.fromInput(typeStr);
                if (type == null) type = PlayerType.AMATER;

                boolean exists = false;
                for (Player p : target) {
                    if (p.getName().equalsIgnoreCase(name)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    target.add(new Player(name, age, type));
                }
            }
        }
    }

    /**
     * Persists the given list of players to {@code players.csv}.
     *
     * @param players players to save
     * @throws Exception if file I/O fails
     */
    public static void savePlayers(List<Player> players) throws Exception {
        ensureDataDir();
        try (var writer = Files.newBufferedWriter(PLAYERS_CSV, StandardCharsets.UTF_8)) {
            writer.write("Meno;Vek;Typ");
            writer.newLine();
            for (Player p : players) {
                writer.write(p.getName() + ";" + p.getAge() + ";" + p.getType().getDisplayName());
                writer.newLine();
            }
        }
    }

    // ====================== MATCHES ======================

    /**
     * Loads all matches from {@code matches.csv}.
     *
     * @param players list of known players used to resolve player references
     * @return list of loaded matches
     * @throws Exception if file I/O fails
     */
    public static List<Match> loadMatchesFromCsv(List<Player> players) throws Exception {
        List<Match> matches = new ArrayList<>();
        loadMatches(matches, players);
        return matches;
    }

    /**
     * Loads matches from {@code matches.csv} into the provided target list.
     * Only valid matches with existing players and valid scores are loaded.
     *
     * @param target  target list to be filled with matches
     * @param players list of known players used to resolve player references
     * @throws Exception if file I/O fails
     */
    public static void loadMatches(List<Match> target, List<Player> players) throws Exception {
        ensureDataDir();

        if (!Files.exists(MATCHES_CSV)) {
            try (var w = Files.newBufferedWriter(MATCHES_CSV, StandardCharsets.UTF_8)) {
                w.write("HracA;HracB;Vysledok;Datum");
                w.newLine();
            }
            return;
        }

        try (var reader = Files.newBufferedReader(MATCHES_CSV, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                if (first) {
                    first = false;
                    if (line.toLowerCase().startsWith("hraca;")) continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length < 4) continue;

                String nameA = parts[0].trim();
                String nameB = parts[1].trim();
                String score = parts[2].trim();
                String dateStr = parts[3].trim();

                Player a = findPlayerByExactName(players, nameA);
                Player b = findPlayerByExactName(players, nameB);
                if (a == null || b == null) continue;

                if (!isValidScore(score)) continue;

                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr);
                } catch (Exception e) {
                    continue;
                }

                if (matchExists(target, a, b, score, date)) continue;

                target.add(new Match(a, b, score, date));
            }
        }
    }

    /**
     * Persists the given list of matches to {@code matches.csv}.
     *
     * @param matches matches to save
     * @throws Exception if file I/O fails
     */
    public static void saveMatches(List<Match> matches) throws Exception {
        ensureDataDir();
        try (var writer = Files.newBufferedWriter(MATCHES_CSV, StandardCharsets.UTF_8)) {
            writer.write("HracA;HracB;Vysledok;Datum");
            writer.newLine();
            for (Match m : matches) {
                writer.write(m.getPlayerA().getName() + ";" +
                        m.getPlayerB().getName() + ";" +
                        m.getScore() + ";" +
                        m.getDate());
                writer.newLine();
            }
        }
    }

    // ====================== HELPER METHODS ======================

    private static Player findPlayerByExactName(List<Player> players, String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    private static boolean isValidScore(String score) {
        String[] sets = score.split(",");
        for (String set : sets) {
            set = set.trim();
            if (!set.matches("\\d:\\d")) return false;

            String[] parts = set.split(":");
            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);

            boolean valid =
                    (a == 6 && b <= 4) || (b == 6 && a <= 4) ||
                            (a == 7 && (b == 5 || b == 6)) ||
                            (b == 7 && (a == 5 || a == 6));

            if (!valid) return false;
        }
        return true;
    }

    private static boolean matchExists(List<Match> list, Player a, Player b, String score, LocalDate date) {
        for (Match m : list) {
            boolean sameOrder =
                    m.getPlayerA().getName().equalsIgnoreCase(a.getName()) &&
                            m.getPlayerB().getName().equalsIgnoreCase(b.getName());
            boolean swappedOrder =
                    m.getPlayerA().getName().equalsIgnoreCase(b.getName()) &&
                            m.getPlayerB().getName().equalsIgnoreCase(a.getName());
            if ((sameOrder || swappedOrder)
                    && m.getScore().equalsIgnoreCase(score)
                    && m.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    private static void ensureDataDir() throws Exception {
        if (!Files.exists(DATA_DIR)) {
            Files.createDirectories(DATA_DIR);
        }
    }
}