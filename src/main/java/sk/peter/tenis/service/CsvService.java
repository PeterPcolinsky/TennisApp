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
 * CSV I/O služba pre hráčov a zápasy (adresár data/*.csv).
 * Formáty:
 * - Players: "Meno;Vek;Typ"
 * - Matches: "HracA;HracB;Vysledok;Datum"
 */
public final class CsvService {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path PLAYERS_CSV = DATA_DIR.resolve("players.csv");
    private static final Path MATCHES_CSV = DATA_DIR.resolve("matches.csv");

    private CsvService() { }

    // ====================== PLAYERS ======================

    /**
     * Načíta hráčov z players.csv do cieľového zoznamu.
     * Ak súbor neexistuje, vytvorí sa s hlavičkou.
     * Riadky s neplatným vekom preskočí; typ sa mapuje cez PlayerType.fromInput(..).
     * Duplicitám (podľa mena, case-insensitive) sa zabraňuje.
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
                    // neplatný vek → preskoč
                    continue;
                }

                PlayerType type = PlayerType.fromInput(typeStr);
                if (type == null) type = PlayerType.AMATER;

                boolean exists = false;
                for (Player p : target) {
                    if (p.getName().equalsIgnoreCase(name)) { exists = true; break; }
                }
                if (!exists) {
                    target.add(new Player(name, age, type));
                }
            }
        }
    }

    /**
     * Zapíše všetkých hráčov z pamäte do players.csv (prepíše celý súbor).
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
     * Načíta zápasy z matches.csv do cieľového zoznamu.
     * Overí existenciu hráčov, platnosť skóre aj dátumu.
     * Duplicitám sa zabraňuje (rovnakí hráči v ľubovoľnom poradí + rovnaké skóre + rovnaký dátum).
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
     * Zapíše všetky zápasy z pamäte do matches.csv (prepíše celý súbor).
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

    // ====================== POMOCNÉ METÓDY ======================

    /**
     * Nájde hráča podľa presného mena (case-insensitive). Ak neexistuje, vráti null.
     */
    private static Player findPlayerByExactName(List<Player> players, String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    /**
     * Overí tenisové skóre setov: formát "X:Y" a povolené hodnoty 6:0–7:6.
     */
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

    /**
     * Zistí, či rovnocenný zápas už existuje (hráči v ľubovoľnom poradí + rovnaký dátum + skóre).
     */
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

    /**
     * Zabezpečí existenciu adresára data/.
     */
    private static void ensureDataDir() throws Exception {
        if (!Files.exists(DATA_DIR)) {
            Files.createDirectories(DATA_DIR);
        }
    }
}