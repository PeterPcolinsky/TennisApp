package sk.peter.tenis.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.PlayerStatsDto;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.dto.LeaderboardDto;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import org.springframework.context.annotation.Profile;

/**
 * Služba pre výpočet štatistík hráčov na základe CSV dát.
 */
@Service
@Profile("h2")
public class StatsService {

    private final MatchService matchService;

    public StatsService(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * Vypočíta štatistiky pre daného hráča.
     *
     * @param playerName meno hráča, napr. "Novak Djokovic"
     * @return PlayerStatsDto s výhrami, prehrami a percentom úspešnosti
     */
    public PlayerStatsDto getStatsForPlayer(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name must not be empty.");
        }

        String target = playerName.trim().toLowerCase();

        // 1️⃣ Načítame všetky zápasy
        List<Match> allMatches = matchService.findAll();

        // 2️⃣ Filtrovanie – len zápasy, kde hrá tento hráč
        List<Match> playerMatches = allMatches.stream()
                .filter(m -> involves(m, target))
                .toList();

        int total = playerMatches.size();
        int wins = 0;
        int losses = 0;

        // 3️⃣ Pre každý zápas určíme, či hráč vyhral alebo prehral
        for (Match m : playerMatches) {
            Player a = m.getPlayerA();
            Player b = m.getPlayerB();
            String score = m.getScore();

            if (score == null || !score.contains(":")) continue;

            String[] sets = score.split(",");
            int setsA = 0;
            int setsB = 0;

            for (String s : sets) {
                String[] games = s.trim().split(":");
                if (games.length != 2) continue;
                try {
                    int gamesA = Integer.parseInt(games[0].trim());
                    int gamesB = Integer.parseInt(games[1].trim());
                    if (gamesA > gamesB) setsA++;
                    else if (gamesB > gamesA) setsB++;
                } catch (NumberFormatException ignored) {
                }
            }

            if (setsA == 0 && setsB == 0) continue; // žiadny validný set

            String winner = setsA > setsB ? a.getName().toLowerCase() : b.getName().toLowerCase();

            if (winner.equals(target)) wins++;
            else losses++;
        }

        double winRate = total == 0 ? 0.0 : round((wins * 100.0) / total);

        return new PlayerStatsDto(playerName, total, wins, losses, winRate);
    }

    // Pomocné metódy
    private boolean involves(Match m, String target) {
        return m.getPlayerA().getName().equalsIgnoreCase(target)
                || m.getPlayerB().getName().equalsIgnoreCase(target);
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    public List<LeaderboardDto> getLeaderboard() {
        try {
            // načítaj všetkých hráčov
            List<Player> players = new ArrayList<>();
            CsvService.loadPlayers(players);

            // načítaj všetky zápasy
            List<Match> matches = matchService.findAll();

            List<LeaderboardDto> leaderboard = new ArrayList<>();

            for (Player player : players) {
                String name = player.getName().trim();

                // zistíme len tie zápasy, kde hral tento hráč
                List<Match> playerMatches = matches.stream()
                        .filter(m -> m.getPlayerA().getName().equalsIgnoreCase(name)
                                || m.getPlayerB().getName().equalsIgnoreCase(name))
                        .toList();

                int total = playerMatches.size();
                int wins = 0;
                int losses = 0;

                for (Match m : playerMatches) {
                    String score = m.getScore();
                    if (score == null || !score.contains(":")) continue;

                    String[] sets = score.split(",");
                    int setsA = 0;
                    int setsB = 0;

                    for (String s : sets) {
                        String[] games = s.trim().split(":");
                        if (games.length != 2) continue;
                        try {
                            int gamesA = Integer.parseInt(games[0].trim());
                            int gamesB = Integer.parseInt(games[1].trim());
                            if (gamesA > gamesB) setsA++;
                            else if (gamesB > gamesA) setsB++;
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    if (setsA == 0 && setsB == 0) continue;

                    String winner = setsA > setsB
                            ? m.getPlayerA().getName().toLowerCase()
                            : m.getPlayerB().getName().toLowerCase();

                    if (winner.equals(name.toLowerCase())) wins++;
                    else losses++;
                }

                double winRate = total == 0 ? 0.0 : Math.round((wins * 1000.0 / total)) / 10.0;

                leaderboard.add(new LeaderboardDto(name, total, wins, losses, winRate));
            }

            // zoradíme podľa výhernosťi zostupne
            return leaderboard.stream()
                    .sorted(Comparator.comparingDouble(LeaderboardDto::getWinRatePercent).reversed())
                    .toList();

        } catch (Exception e) {
            return List.of();
        }
    }

    private LocalDate parseDateSafe(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    public LeaderboardDto getPlayerStats(String playerName, String from, String to) {
        if (playerName == null || playerName.isBlank()) return null;

        try {
            List<Match> matches = matchService.findAll();

            // final / effectively final lokálne premenné
            final String target = playerName.trim();
            final LocalDate fromDate = parseDateSafe(from);
            final LocalDate toDate = parseDateSafe(to);

            List<Match> playerMatches = matches.stream()
                    .filter(m -> m.getPlayerA().getName().equalsIgnoreCase(target)
                            || m.getPlayerB().getName().equalsIgnoreCase(target))
                    .filter(m -> (fromDate == null || !m.getDate().isBefore(fromDate))
                            && (toDate == null || !m.getDate().isAfter(toDate)))
                    .toList();

            int total = playerMatches.size();
            int wins = 0;
            int losses = 0;

            for (Match m : playerMatches) {
                String score = m.getScore();
                if (score == null || !score.contains(":")) continue;

                String[] sets = score.split(",");
                int setsA = 0, setsB = 0;

                for (String s : sets) {
                    String[] games = s.trim().split(":");
                    if (games.length != 2) continue;
                    try {
                        int gamesA = Integer.parseInt(games[0].trim());
                        int gamesB = Integer.parseInt(games[1].trim());
                        if (gamesA > gamesB) setsA++;
                        else if (gamesB > gamesA) setsB++;
                    } catch (NumberFormatException ignored) {
                    }
                }

                if (setsA == 0 && setsB == 0) continue;

                String winner = (setsA > setsB)
                        ? m.getPlayerA().getName()
                        : m.getPlayerB().getName();

                if (winner.equalsIgnoreCase(target)) wins++;
                else losses++;
            }

            double winRate = total == 0 ? 0.0 : Math.round((wins * 1000.0 / total)) / 10.0;
            return new LeaderboardDto(playerName.trim(), total, wins, losses, winRate);

        } catch (Exception e) {
            return null;
        }
    }

    // 👇 Pôvodná pomocná metóda z konzolovej verzie
    public double calcWinRate(int wins, int losses) {
        int finished = wins + losses;
        if (finished == 0) return 0.0;
        return (wins * 100.0) / finished;
    }
}