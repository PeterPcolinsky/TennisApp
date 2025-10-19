package sk.peter.tenis.service;

import sk.peter.tenis.model.Player;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.model.Match;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

/**
 * CSV I/O for players and matches (data/*.csv).
 * Players: "Meno;Vek;Typ"
 * Matches: "HracA;HracB;Vysledok;Datum"
 */
public final class CsvService {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path PLAYERS_CSV = DATA_DIR.resolve("players.csv");
    private static final Path MATCHES_CSV = DATA_DIR.resolve("matches.csv");

    private CsvService() { }

    // ============================================================
    // === PLAYERS =================================================
    // ============================================================

    /**
     * Loads players from players.csv into the in-memory list.
     * Creates the file with header if missing.
     */
    public static void loadPlayers(List<Player> target) throws Exception {
        ensureDataDir();

        // Ak CSV ešte neexistuje, vytvor ho s hlavičkou
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

                // preskoč hlavičku
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
                    // neplatný vek – preskoč riadok
                    continue;
                }

                // Podporí "Amatér", "Amater", "Profesionál", "Profesional"
                PlayerType type = PlayerType.fromInput(typeStr);
                if (type == null) {
                    // fallback (napr. ak by niekto ručne prepísal CSV)
                    type = PlayerType.AMATER;
                }

                // vyhne sa duplicitám podľa mena
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
     * Writes all players from memory into players.csv (overwrites the file).
     */
    public static void savePlayers(List<Player> players) throws Exception {
        ensureDataDir();

        // prepíšeme celý súbor vždy nanovo – jednoduché a bezpečné
        try (var writer = Files.newBufferedWriter(PLAYERS_CSV, StandardCharsets.UTF_8)) {
            writer.write("Meno;Vek;Typ");
            writer.newLine();

            for (Player p : players) {
                writer.write(p.getName() + ";" + p.getAge() + ";" + p.getType().getDisplayName());
                writer.newLine();
            }
        }
    }

    // ============================================================
    // === MATCHES ================================================
    // ============================================================

    /**
     * Loads matches from matches.csv into target list.
     * Validates score format and date, skips invalid rows and duplicates
     * (duplicate = same players in any order + same score + same date).
     */
    public static void loadMatches(List<Match> target, List<Player> players) throws Exception {
        ensureDataDir();

        // Ak CSV neexistuje, vytvor s hlavičkou
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

                // preskoč hlavičku
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

                // nájdi hráčov
                Player a = findPlayerByExactName(players, nameA);
                Player b = findPlayerByExactName(players, nameB);
                if (a == null || b == null) {
                    continue;
                }

                // validuj skóre
                if (!isValidScore(score)) {
                    continue;
                }

                // parsuj dátum
                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr);
                } catch (Exception e) {
                    continue;
                }

                // zabráň duplicitám
                if (matchExists(target, a, b, score, date)) {
                    continue;
                }

                target.add(new Match(a, b, score, date));
            }
        }
    }

    /**
     * Writes all matches from memory into matches.csv (overwrites the file).
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

    // ============================================================
    // === PRIVATE HELPERS ========================================
    // ============================================================

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
            if (!set.matches("\\d:\\d")) {
                return false;
            }

            String[] parts = set.split(":");
            int gamesA = Integer.parseInt(parts[0]);
            int gamesB = Integer.parseInt(parts[1]);

            boolean valid =
                    (gamesA == 6 && gamesB <= 4) ||
                            (gamesB == 6 && gamesA <= 4) ||
                            (gamesA == 7 && (gamesB == 5 || gamesB == 6)) ||
                            (gamesB == 7 && (gamesA == 5 || gamesA == 6));

            if (!valid) {
                return false;
            }
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