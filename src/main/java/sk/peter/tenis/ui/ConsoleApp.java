package sk.peter.tenis.ui;

import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.service.CsvService;
import sk.peter.tenis.util.Printer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    // In-memory dáta počas behu aplikácie
    private final List<Player> players = new ArrayList<>();
    private final List<Match> matches = new ArrayList<>();

    /**
     * Hlavná slučka konzolovej aplikácie:
     * - načíta hráčov a zápasy z CSV (cez CsvService),
     * - zobrazuje menu a spracúva voľby používateľa.
     */
    public void run() {
        Printer.println("🎾 Vitaj v TenisApp!");
        Printer.println("Cieľ: postupne vybudovať robustnú appku (hráči, zápasy, štatistiky).");

        // Načítanie hráčov z CSV (CsvService)
        try {
            CsvService.loadPlayers(players);
            Printer.println("🔄 Načítaných hráčov: " + players.size());
        } catch (Exception e) {
            Printer.println("⚠️ Načítanie CSV zlyhalo: " + e.getMessage());
        }

        // Načítanie zápasov z CSV (CsvService)
        try {
            CsvService.loadMatches(matches, players);
            Printer.println("🔄 Načítaných zápasov: " + matches.size());
        } catch (Exception e) {
            Printer.println("⚠️ Načítanie zápasov CSV zlyhalo: " + e.getMessage());
        }

        Scanner sc = new Scanner(System.in);
        int choice = -1;

        // Hlavné menu
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
            Printer.println("9) Zápasy hráča");
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
                case 9 -> showMatchesByPlayer(sc);
                case 0 -> Printer.println("Koniec. 👋");
                default -> Printer.println("Neznáma voľba.");
            }
        }
    }

    // ====================== VOĽBY MENU ======================

    /**
     * Mini demo: vypočíta výhernosť z počtu výhier a prehier.
     */
    private void calcWinRateDemo(Scanner sc) {
        int wins = readIntInRange(sc, "Zadaj počet výhier: ", 0, 1000);
        int losses = readIntInRange(sc, "Zadaj počet prehier: ", 0, 1000);
        //StatsService stats = new StatsService();
        //double winRate = stats.calcWinRate(wins, losses);
        //Printer.println("Výhernosť: " + winRate + " %");
    }

    /**
     * Zaregistruje nového hráča (overenie mena, veku a typu), pridá ho do zoznamu
     * a uloží aktuálny zoznam hráčov do players.csv (cez CsvService).
     */
    private void registerPlayer(Scanner sc) {
        String name = readName(sc, "Zadaj meno hráča: ");                // validácia mena
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
            CsvService.savePlayers(players);
            Printer.println("💾 Hráči boli uložené (players.csv).");
        } catch (Exception e) {
            Printer.println("⚠️ Nepodarilo sa uložiť CSV: " + e.getMessage());
        }

        Printer.println("✅ Hráč bol úspešne zaregistrovaný:");
        Printer.println(player.toString());
    }

    /**
     * Vypíše všetkých hráčov a ich základné údaje.
     */
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

    /**
     * Vyhľadá hráča podľa zadanej časti mena (bez ohľadu na veľkosť písmen).
     */
    private void findPlayerByName(Scanner sc) {
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

    /**
     * Odstráni hráča podľa presného mena (bez ohľadu na veľkosť písmen).
     */
    /**
     * Odstráni hráča podľa presného mena (case-insensitive) a
     * perzistentne uloží zmeny do players.csv. Zároveň zmaže
     * všetky zápasy, v ktorých hráč vystupoval, a uloží matches.csv.
     */
    private void removePlayerByName(Scanner sc) {
        String name = readName(sc, "Zadaj meno hráča, ktorého chceš odstrániť: ");

        // Nájdeme konkrétny objekt hráča (neodstraňujme, kým ho nemáme)
        Player player = findPlayerByExactName(name);
        if (player == null) {
            Printer.println("⚠️ Hráč s menom '" + name + "' sa nenašiel.");
            return;
        }

        // 1) Zmažeme všetky jeho zápasy (A alebo B)
        int removedMatches = removeMatchesOf(player);

        // 2) Zmažeme hráča zo zoznamu
        boolean removedPlayer = players.removeIf(p -> p.getName().equalsIgnoreCase(name));

        // 3) Uložíme CSV (hráči aj zápasy)
        try {
            CsvService.savePlayers(players);
            CsvService.saveMatches(matches);
        } catch (Exception e) {
            Printer.println("⚠️ Chyba pri ukladaní CSV: " + e.getMessage());
            // aj keď ukladanie zlyhá, v pamäti už odstránené je; používateľa o tom informujeme
        }

        if (removedPlayer) {
            Printer.println("✅ Hráč '" + name + "' bol odstránený.");
            Printer.println("🧹 Zmazané zápasy s týmto hráčom: " + removedMatches);
        } else {
            // teoreticky by sme sem už nemali spadnúť, keďže player != null
            Printer.println("⚠️ Hráča sa nepodarilo odstrániť.");
        }
    }

    /**
     * Pridá nový zápas (kontroluje: existenciu hráčov, že nejde o toho istého hráča,
     * validný formát skóre a dátumu) a uloží zápasy do matches.csv (cez CsvService).
     */
    private void addMatch(Scanner sc) {
        if (players.size() < 2) {
            Printer.println("⚠️ Potrebuješ aspoň dvoch hráčov, aby si mohol pridať zápas.");
            return;
        }

        String nameA = readName(sc, "Zadaj meno hráča A: "); // validácia mena
        Player playerA = findPlayerByExactName(nameA);
        if (playerA == null) {
            Printer.println("⚠️ Hráč '" + nameA + "' neexistuje. Najprv ho zaregistruj.");
            return;
        }

        String nameB = readName(sc, "Zadaj meno hráča B: "); // validácia mena
        Player playerB = findPlayerByExactName(nameB);
        if (playerB == null) {
            Printer.println("⚠️ Hráč '" + nameB + "' neexistuje. Najprv ho zaregistruj.");
            return;
        }

        if (playerA.getName().equalsIgnoreCase(playerB.getName())) {
            Printer.println("⚠️ Hráč A a hráč B nemôžu byť ten istý človek.");
            return;
        }

        // Zadanie a validácia skóre (napr. 6:4, 3:6, 7:6)
        String score;
        while (true) {
            score = readNonEmpty(sc, "Zadaj výsledok (napr. 6:4, 3:6, 7:6): ");
            if (isValidScore(score)) break;
            Printer.println("Skús znova.");
        }

        // Zadanie a validácia dátumu (YYYY-MM-DD)
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

        // Uloženie do CSV (CsvService)
        try {
            CsvService.saveMatches(matches);
            Printer.println("💾 Zápasy boli uložené (matches.csv).");
        } catch (Exception e) {
            Printer.println("⚠️ Nepodarilo sa uložiť zápasy: " + e.getMessage());
        }

        Printer.println("✅ Zápas pridaný: " + m.toString());
    }

    /**
     * Vypíše všetky zápasy v jednoduchom formáte.
     */
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

    /**
     * Zobrazí základné štatistiky pre konkrétneho hráča:
     * počet zápasov, výhry/prehry, nedokončené zápasy a výhernosť (len z dokončených).
     */
    private void showPlayerStats(Scanner sc) {
        String name = readName(sc, "Meno hráča pre štatistiky: "); // validácia mena
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

    // ====================== POMOCNÉ METÓDY ======================

    /**
     * Bezpečne načíta celé číslo v danom rozsahu [min..max] zo vstupu.
     */
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

    /**
     * Načíta neprázdny reťazec zo vstupu (použité pre polia, kde netreba špeciálnu validáciu).
     */
    private String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            Printer.println(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            Printer.println("Toto pole nemôže byť prázdne.");
        }
    }

    /**
     * Načíta a overí meno: iba písmená (vrátane diakritiky) a medzery, dĺžka 2–40 znakov.
     * Medzery normalizuje na jednotnú podobu.
     */
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

    /**
     * Načíta fragment mena (písmená/medzery 1–40) – používa sa pri vyhľadávaní.
     */
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

    /**
     * Nájde hráča podľa presného mena (bez ohľadu na veľkosť písmen); ak neexistuje, vráti null.
     */
    private Player findPlayerByExactName(String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    /**
     * Zistí, či sa hráč zúčastnil daného zápasu.
     */
    private boolean participated(Player p, Match m) {
        return m.getPlayerA().getName().equalsIgnoreCase(p.getName())
                || m.getPlayerB().getName().equalsIgnoreCase(p.getName());
    }

    /**
     * Vyhodnotí výsledok zápasu z pohľadu hráča:
     * - vráti 1 ak hráč vyhral, 0 ak prehral, alebo null ak sú sety vyrovnané (nedokončené).
     */
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

    /**
     * Overí platnosť skóre setov (formát X:Y, povolené tenisové výsledky 6:0–7:6).
     * Skóre je zoznam setov oddelených čiarkou (napr. "6:4, 3:6, 7:6").
     */
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

    /**
     * Vypíše zápasy, ktorých sa zúčastnil zadaný hráč (podľa presného mena, case-insensitive).
     * Ak hráč neexistuje alebo nemá zápasy, vypíše vhodnú správu.
     */
    private void showMatchesByPlayer(Scanner sc) {
        Printer.println("=== Zápasy hráča ===");
        Printer.println("Zadaj meno hráča: ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            Printer.println("⚠️ Meno nesmie byť prázdne.");
            return;
        }

        Player player = findPlayerByName(input);
        if (player == null) {
            Printer.println("⚠️ Hráč nenájdený: " + input);
            return;
        }

        int count = 0;
        for (Match m : matches) {
            if (m.getPlayerA() == player || m.getPlayerB() == player) {
                if (count == 0) {
                    Printer.println("— Zápasy pre hráča: " + player.getName());
                }
                Printer.println((count + 1) + ") " + formatMatchSimple(m));
                count++;
            }
        }

        if (count == 0) {
            Printer.println("ℹ️ Hráč " + player.getName() + " zatiaľ nemá žiadne zápasy.");
        }
    }

    /**
     * Nájde hráča podľa mena (normalizované na malé písmená, jedny medzery).
     */
    private Player findPlayerByName(String raw) {
        String target = raw.trim().toLowerCase().replaceAll("\\s+", " ");
        for (Player p : players) {
            String n = p.getName() == null ? "" : p.getName().trim().toLowerCase().replaceAll("\\s+", " ");
            if (n.equals(target)) return p;
        }
        return null;
    }

    /**
     * Jednoduchý textový formát jedného zápasu: DÁTUM | HráčA SKÓRE HráčB
     * (napr. 2025-05-10 | Peter 6:4, 6:2, 6:2 Novak)
     */
    private String formatMatchSimple(Match m) {
        String date = (m.getDate() == null) ? "----------" : m.getDate().toString();
        return date + " | " + m.getPlayerA().getName() + " " + m.getScore() + " " + m.getPlayerB().getName();
    }

    /**
     * Zmaže všetky zápasy, v ktorých sa hráč zúčastnil (ako A alebo B).
     * @return počet odstránených zápasov
     */
    private int removeMatchesOf(Player player) {
        String target = player.getName();
        int before = matches.size();
        matches.removeIf(m ->
                m.getPlayerA().getName().equalsIgnoreCase(target) ||
                        m.getPlayerB().getName().equalsIgnoreCase(target)
        );
        return before - matches.size();
    }
}