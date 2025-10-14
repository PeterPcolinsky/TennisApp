package sk.peter.tenis.ui;

import sk.peter.tenis.model.Player;
import sk.peter.tenis.service.StatsService;
import sk.peter.tenis.util.Printer;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.model.Match;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Scanner;


public class ConsoleApp {

    // Cesty k CSV súborom
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path PLAYERS_CSV = DATA_DIR.resolve("players.csv");
    private static final Path MATCHES_CSV = DATA_DIR.resolve("matches.csv");

    private final List<Player> players = new ArrayList<>();
    private final List<Match> matches = new ArrayList<>();

    public void run() {
        Printer.println("🎾 Vitaj v TenisApp!");
        Printer.println("Cieľ: postupne vybudovať robustnú appku (hráči, zápasy, štatistiky).");

        try {
            loadPlayersFromCsv();
            Printer.println("🔄 Načítaných hráčov: " + players.size());
        } catch (Exception e) {
            Printer.println("⚠️ Načítanie CSV zlyhalo: " + e.getMessage());
        }

        try {
            loadMatchesFromCsv();
            Printer.println("🔄 Načítaných zápasov: " + matches.size());
        } catch (Exception e) {
            Printer.println("⚠️ Načítanie zápasov CSV zlyhalo: " + e.getMessage());
        }

        Scanner sc = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            Printer.println("");
            Printer.println("=== MENU ===");
            Printer.println("1) Vypočítaj win-rate (výhernosť)");
            Printer.println("2) Zaregistruj hráča");
            Printer.println("3) Zobraz všetkých hráčov");
            Printer.println("4) Nájsť hráča podľa mena");
            Printer.println("5) Odstráň hráča podľa mena");
            Printer.println("6) Pridaj zápas");
            Printer.println("7) Zobraz všetky zápasy");
            Printer.println("8) Štatistiky hráča");
            Printer.println("0) Koniec");
            Printer.println("-----------------");

            choice = readIntInRange(sc, "Zadaj voľbu: ", 0, 99);

            switch (choice) {
                case 1 -> calcWinRateDemo(sc);
                case 2 -> registerPlayer(sc);
                case 3 -> listPlayers();
                case 4 -> findPlayerByName(sc);
                case 5 -> removePlayerByName(sc);
                case 6 -> addMatch(sc);
                case 7 -> listMatches();
                case 8 -> showPlayerStats(sc);
                case 0 -> Printer.println("Koniec. 👋");
                default -> Printer.println("Neznáma voľba.");
            }
        }
    }

    // ============ VOĽBY MENU ============

    private void calcWinRateDemo(Scanner sc) {
        int wins = readIntInRange(sc, "Zadaj počet výhier: ", 0, 1000);
        int losses = readIntInRange(sc, "Zadaj počet prehier: ", 0, 1000);
        StatsService stats = new StatsService();
        double winRate = stats.calcWinRate(wins, losses);
        Printer.println("Výhernosť: " + winRate + " %");
    }

    private void registerPlayer(Scanner sc) {
        String name = readName(sc, "Zadaj meno hráča: ");                // VALIDÁCIA MENA
        int age = readIntInRange(sc, "Zadaj vek hráča (5–100): ", 5, 100);
        PlayerType type = null;
        while (type == null) {
            String input = readNonEmpty(sc, "Zadaj typ hráča (Amatér/Profesionál): ");
            type = PlayerType.fromInput(input);
            if (type == null) {
                Printer.println("⚠️ Zadaj prosím iba 'Amatér' alebo 'Profesionál'.");
            }
        }

        Player player = new Player(name, age, type);
        players.add(player);
        try {
            savePlayersToCsv();
            Printer.println("💾 Uložené do " + PLAYERS_CSV.toString());
        } catch (Exception e) {
            Printer.println("⚠️ Nepodarilo sa uložiť CSV: " + e.getMessage());
        }

        Printer.println("✅ Hráč bol úspešne zaregistrovaný:");
        Printer.println(player.toString());
    }

    private void listPlayers() {
        Printer.println("=== Zoznam hráčov ===");
        if (players.isEmpty()) {
            Printer.println("Zatiaľ nemáš žiadnych hráčov.");
            return;
        }
        int i = 1;
        for (Player p : players) {
            Printer.println(i + ") " + p.toString());
            i++;
        }
    }

    private void findPlayerByName(Scanner sc) {
        // ponecháme možnosť čiastočného hľadania, ale iba písmená/medzery
        String query = readLettersFragment(sc, "Zadaj meno (alebo časť mena): ");
        String q = query.toLowerCase();

        boolean found = false;
        for (Player p : players) {
            if (p.getName().toLowerCase().contains(q)) {
                if (!found) {
                    Printer.println("=== Nájdení hráči ===");
                    found = true;
                }
                Printer.println(p.toString());
            }
        }
        if (!found) {
            Printer.println("Nenašiel sa žiadny hráč pre: " + query);
        }
    }

    private void removePlayerByName(Scanner sc) {
        String name = readName(sc, "Zadaj meno hráča, ktorého chceš odstrániť: "); // VALIDÁCIA MENA
        boolean removed = players.removeIf(p -> p.getName().equalsIgnoreCase(name));
        if (removed) {
            Printer.println("✅ Hráč " + name + " bol odstránený zo zoznamu.");
        } else {
            Printer.println("⚠️ Hráč s menom '" + name + "' sa nenašiel.");
        }
    }

    private void addMatch(Scanner sc) {
        if (players.size() < 2) {
            Printer.println("⚠️ Potrebuješ aspoň dvoch hráčov, aby si mohol pridať zápas.");
            return;
        }

        String nameA = readName(sc, "Zadaj meno hráča A: "); // VALIDÁCIA MENA
        Player playerA = findPlayerByExactName(nameA);
        if (playerA == null) {
            Printer.println("⚠️ Hráč '" + nameA + "' neexistuje. Najprv ho zaregistruj.");
            return;
        }

        String nameB = readName(sc, "Zadaj meno hráča B: "); // VALIDÁCIA MENA
        Player playerB = findPlayerByExactName(nameB);
        if (playerB == null) {
            Printer.println("⚠️ Hráč '" + nameB + "' neexistuje. Najprv ho zaregistruj.");
            return;
        }

        if (playerA.getName().equalsIgnoreCase(playerB.getName())) {
            Printer.println("⚠️ Hráč A a hráč B nemôžu byť ten istý človek.");
            return;
        }

        String score;
        while (true) {
            score = readNonEmpty(sc, "Zadaj výsledok (napr. 6:4, 3:6, 7:6): ");
            if (isValidScore(score)) break;
            Printer.println("Skús znova.");
        }

        LocalDate date;
        while (true) {
            String d = readNonEmpty(sc, "Zadaj dátum (YYYY-MM-DD): ");
            try {
                date = LocalDate.parse(d);
                break;
            } catch (Exception e) {
                Printer.println("⚠️ Neplatný formát dátumu. Skús znova (napr. 2025-10-12).");
            }
        }

        Match m = new Match(playerA, playerB, score, date);
        matches.add(m);

        try {
            saveMatchesToCsv();
            Printer.println("💾 Uložené do " + MATCHES_CSV.toString());
        } catch (Exception e) {
            Printer.println("⚠️ Nepodarilo sa uložiť zápasy: " + e.getMessage());
        }

        Printer.println("✅ Zápas pridaný: " + m.toString());
    }

    private void listMatches() {
        Printer.println("=== Zoznam zápasov ===");
        if (matches.isEmpty()) {
            Printer.println("Zatiaľ nemáš žiadne zápasy.");
            return;
        }
        int i = 1;
        for (Match m : matches) {
            Printer.println(i + ") " + m.toString());
            i++;
        }
    }

    private void showPlayerStats(Scanner sc) {
        String name = readName(sc, "Meno hráča pre štatistiky: "); // VALIDÁCIA MENA
        Player player = findPlayerByExactName(name);
        if (player == null) {
            Printer.println("⚠️ Hráč '" + name + "' neexistuje.");
            return;
        }

        int played = 0;
        int wins = 0;
        int losses = 0;
        int unfinished = 0;

        for (Match m : matches) {
            if (!participated(player, m)) continue;
            played++;

            Integer res = matchResultFor(player, m); // 1=win, 0=loss, null=unfinished
            if (res == null) {
                unfinished++;
            } else if (res == 1) {
                wins++;
            } else {
                losses++;
            }
        }

        int finished = wins + losses;
        double winRate = (finished == 0) ? 0.0 : (wins * 100.0) / finished;

        Printer.println("=== Štatistiky: " + player.getName() + " ===");
        Printer.println("Zápasy spolu: " + played + " (ukončené: " + finished + ", nedokončené: " + unfinished + ")");
        Printer.println("Výhry: " + wins + " | Prehry: " + losses);
        Printer.println("Win-rate (len ukončené): " + String.format("%.1f", winRate) + " %");
    }

    // ============ POMOCNÉ METÓDY ============

    // bezpečné celé číslo v rozsahu
    private int readIntInRange(Scanner sc, String prompt, int min, int max) {
        while (true) {
            Printer.println(prompt);
            String line = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    Printer.println("Hodnota musí byť v rozsahu " + min + " až " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                Printer.println("Zadaj prosím celé číslo (napr. 7).");
            }
        }
    }

    // neprázdny reťazec (používame pri políčkach, kde netreba regex)
    private String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            Printer.println(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            Printer.println("Toto pole nemôže byť prázdne.");
        }
    }

    // meno: len písmená (aj diakritika) a medzery, dĺžka 2–40
    private String readName(Scanner sc, String prompt) {
        while (true) {
            Printer.println(prompt);
            String s = sc.nextLine().trim();
            s = s.replaceAll("\\s+", " ");
            if (s.matches("[\\p{L} ]{2,40}")) {
                return s;
            }
            Printer.println("⚠️ Meno môže obsahovať iba písmená a medzery (2–40 znakov). Skús znova.");
        }
    }

    // fragment mena pre vyhľadávanie: písmená/medzery, dĺžka 1–40
    private String readLettersFragment(Scanner sc, String prompt) {
        while (true) {
            Printer.println(prompt);
            String s = sc.nextLine().trim();
            s = s.replaceAll("\\s+", " ");
            if (s.matches("[\\p{L} ]{1,40}")) {
                return s;
            }
            Printer.println("⚠️ Použi iba písmená a medzery (1–40 znakov).");
        }
    }

    private Player findPlayerByExactName(String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    private boolean participated(Player p, Match m) {
        return m.getPlayerA().getName().equalsIgnoreCase(p.getName())
                || m.getPlayerB().getName().equalsIgnoreCase(p.getName());
    }

    // 1 = win, 0 = loss, null = unfinished (rovnosť setov)
    private Integer matchResultFor(Player player, Match m) {
        boolean isA = m.getPlayerA().getName().equalsIgnoreCase(player.getName());
        int setsA = 0;
        int setsB = 0;

        String[] sets = m.getScore().split(",");
        for (String s : sets) {
            String[] parts = s.trim().split(":");
            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);
            if (a > b) setsA++;
            else if (b > a) setsB++;
        }

        if (setsA == setsB) return null;
        boolean aWon = (setsA > setsB);
        boolean playerWon = (isA == aWon);
        return playerWon ? 1 : 0;
    }

    // validácia skóre setov: správny formát a povolené výsledky
    private boolean isValidScore(String score) {
        String[] sets = score.split(",");

        for (String set : sets) {
            set = set.trim();
            if (!set.matches("\\d:\\d")) {
                Printer.println("⚠️ Nesprávny formát setu: " + set + " (použi napr. 6:4)");
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
                Printer.println("⚠️ Nepovolený výsledok setu: " + set);
                return false;
            }
        }
        return true;
    }

    private void ensureDataDir() throws Exception {
        if (!Files.exists(DATA_DIR)) {
            Files.createDirectories(DATA_DIR);
        }
    }

    private void savePlayersToCsv() throws Exception {
        ensureDataDir();
        // prepíšeme celý súbor vždy nanovo – jednoduché a bezpečné
        try (var writer = Files.newBufferedWriter(PLAYERS_CSV, StandardCharsets.UTF_8)) {
            // hlavička (voliteľné)
            writer.write("Meno;Vek;Typ");
            writer.newLine();

            for (Player p : players) {
                // meno ani typ neobsahujú ; (validujeme len písmená/medzery)
                writer.write(p.getName() + ";" + p.getAge() + ";" + p.getType().getDisplayName());
                writer.newLine();
            }
        }
    }

    private void saveMatchesToCsv() throws Exception {
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

    private void loadPlayersFromCsv() throws Exception {
        ensureDataDir();

        // Ak CSV ešte neexistuje, vytvor ho s hlavičkou a skonči
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
                var type = sk.peter.tenis.model.PlayerType.fromInput(typeStr);
                if (type == null) {
                    // fallback (napr. ak by niekto ručne prepísal CSV)
                    type = sk.peter.tenis.model.PlayerType.AMATER;
                }

                // vyhne sa duplicitám podľa mena
                if (findPlayerByExactName(name) == null) {
                    players.add(new sk.peter.tenis.model.Player(name, age, type));
                }
            }
        }
    }

    private void loadMatchesFromCsv() throws Exception {
        ensureDataDir();

        // Ak CSV neexistuje, vytvor s hlavičkou a skonči
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

                // nájdi hráčov (musia existovať – hráčov načítavame skôr)
                Player a = findPlayerByExactName(nameA);
                Player b = findPlayerByExactName(nameB);
                if (a == null || b == null) {
                    Printer.println("⚠️ Riadok preskočený – hráč A/B sa nenašiel: " + nameA + " / " + nameB);
                    continue;
                }

                // validuj skóre
                if (!isValidScore(score)) {
                    Printer.println("⚠️ Riadok preskočený – neplatné skóre: " + score);
                    continue;
                }

                // parsuj dátum
                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr);
                } catch (Exception e) {
                    Printer.println("⚠️ Riadok preskočený – neplatný dátum: " + dateStr);
                    continue;
                }

                // zabráň duplicitám (rovnakí hráči + dátum + skóre; porovnáme aj prehodené poradie)
                if (matchExists(a, b, score, date)) {
                    continue;
                }

                matches.add(new Match(a, b, score, date));
            }
        }
    }

    private boolean matchExists(Player a, Player b, String score, LocalDate date) {
        for (Match m : matches) {
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

}

