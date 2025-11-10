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

/**
 * Služba pre výpočet štatistík hráčov na základe CSV dát.
 */
@Service
@Profile({"h2", "mysql"})
public class StatsService {

    private final MatchService matchService;

    public StatsService(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * Vypočíta štatistiky pre daného hráča.
     */
    public PlayerStatsDto getStatsForPlayer(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name must not be empty.");
        }

        String target = playerName.trim().toLowerCase();
        List<Match> allMatches = matchService.findAll();
        List<Match> playerMatches = allMatches.stream()
                .filter(m -> involves(m, target))
                .toList();

        int total = playerMatches.size();
        int wins = 0;
        int losses = 0;

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
                } catch (NumberFormatException ignored) {}
            }

            if (setsA == 0 && setsB == 0) continue;

            String winner = setsA > setsB ? a.getName().toLowerCase() : b.getName().toLowerCase();
            if (winner.equals(target)) wins++;
            else losses++;
        }

        double winRate = total == 0 ? 0.0 : round((wins * 100.0) / total);
        return new PlayerStatsDto(playerName, total, wins, losses, winRate);
    }

    private boolean involves(Match m, String target) {
        return m.getPlayerA().getName().equalsIgnoreCase(target)
                || m.getPlayerB().getName().equalsIgnoreCase(target);
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    /**
     * Vytvorí leaderboard zoradený podľa percenta výhier.
     */
    public List<LeaderboardDto> getLeaderboard() {
        try {
            List<Player> players = new ArrayList<>();
            CsvService.loadPlayers(players);
            List<Match> matches = matchService.findAll();
            List<LeaderboardDto> leaderboard = new ArrayList<>();

            for (Player player : players) {
                String name = player.getName().trim();
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
                        } catch (NumberFormatException ignored) {}
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

            return leaderboard.stream()
                    .filter(p -> p.getMatches() > 0 && p.getWinRatePercent() > 0)
                    .sorted(Comparator.comparingDouble(LeaderboardDto::getWinRatePercent).reversed())
                    .toList();

        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Nová verzia – používa LocalDate pre rozsahy dátumov.
     */
    public PlayerStatsDto getPlayerStats(String playerName, LocalDate from, LocalDate to) {
        if (playerName == null || playerName.isBlank()) return null;

        try {
            List<Match> matches = matchService.findAll();
            final String target = playerName.trim();

            List<Match> playerMatches = matches.stream()
                    .filter(m -> m.getPlayerA().getName().equalsIgnoreCase(target)
                            || m.getPlayerB().getName().equalsIgnoreCase(target))
                    .filter(m -> (from == null || !m.getDate().isBefore(from.minusDays(1)))
                            && (to == null || !m.getDate().isAfter(to.plusDays(1))))
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
                    } catch (NumberFormatException ignored) {}
                }

                if (setsA == 0 && setsB == 0) continue;
                String winner = (setsA > setsB)
                        ? m.getPlayerA().getName()
                        : m.getPlayerB().getName();

                if (winner.equalsIgnoreCase(target)) wins++;
                else losses++;
            }

            double winRate = total == 0 ? 0.0 : Math.round((wins * 1000.0 / total)) / 10.0;
            return new PlayerStatsDto(playerName.trim(), total, wins, losses, winRate);

        } catch (Exception e) {
            return null;
        }
    }

    public double calcWinRate(int wins, int losses) {
        int finished = wins + losses;
        if (finished == 0) return 0.0;
        return (wins * 100.0) / finished;
    }
}