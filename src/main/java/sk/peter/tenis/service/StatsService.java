package sk.peter.tenis.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.LeaderboardDto;
import sk.peter.tenis.dto.PlayerStatsDto;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.repository.MatchRepository;
import sk.peter.tenis.repository.PlayerRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Služba pre výpočet štatistík hráčov na základe dát z DB (MySQL/H2).
 */
@Service
@Profile({"h2", "mysql"})
public class StatsService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    public StatsService(MatchRepository matchRepository,
                        PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * Pôvodná metóda – štatistiky pre daného hráča (bez dátumového rozsahu).
     * Teraz používa zápasy z DB (MatchEntity).
     */
    public PlayerStatsDto getStatsForPlayer(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name must not be empty.");
        }

        final String target = playerName.trim().toLowerCase();

        List<MatchEntity> allMatches = matchRepository.findAll();

        List<MatchEntity> playerMatches = allMatches.stream()
                .filter(m -> involves(m, target))
                .toList();

        int total = playerMatches.size();
        int wins = 0;
        int losses = 0;

        for (MatchEntity m : playerMatches) {
            String score = m.getResult();
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

            if (winner.equals(target)) wins++;
            else losses++;
        }

        double winRate = total == 0 ? 0.0 : round((wins * 100.0) / total);
        return new PlayerStatsDto(playerName, total, wins, losses, winRate);
    }

    private boolean involves(MatchEntity m, String target) {
        return m.getPlayerA().getName().equalsIgnoreCase(target)
                || m.getPlayerB().getName().equalsIgnoreCase(target);
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    /**
     * Leaderboard – teraz kompletne z DB (players + matches).
     */
    public List<LeaderboardDto> getLeaderboard() {
        try {
            List<PlayerEntity> players = playerRepository.findAll();
            List<MatchEntity> matches = matchRepository.findAll();

            return players.stream()
                    .map(player -> computeLeaderboardRow(player, matches))
                    .filter(row -> row.getMatches() > 0 && row.getWinRatePercent() > 0)
                    .sorted(Comparator.comparingDouble(LeaderboardDto::getWinRatePercent).reversed())
                    .toList();

        } catch (Exception e) {
            return List.of();
        }
    }

    private LeaderboardDto computeLeaderboardRow(PlayerEntity player, List<MatchEntity> allMatches) {
        String name = player.getName().trim();
        String lowerName = name.toLowerCase(Locale.ROOT);

        List<MatchEntity> playerMatches = allMatches.stream()
                .filter(m ->
                        m.getPlayerA().getName().equalsIgnoreCase(name)
                                || m.getPlayerB().getName().equalsIgnoreCase(name))
                .toList();

        int total = playerMatches.size();
        int wins = 0;
        int losses = 0;

        for (MatchEntity m : playerMatches) {
            String score = m.getResult();
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

            String winner = (setsA > setsB)
                    ? m.getPlayerA().getName().toLowerCase(Locale.ROOT)
                    : m.getPlayerB().getName().toLowerCase(Locale.ROOT);

            if (winner.equals(lowerName)) wins++;
            else losses++;
        }

        double winRate = total == 0 ? 0.0 : Math.round((wins * 1000.0 / total)) / 10.0;
        return new LeaderboardDto(name, total, wins, losses, winRate);
    }

    /**
     * Nová verzia – používa LocalDate pre rozsahy dátumov, teraz tiež z DB.
     */
    public PlayerStatsDto getPlayerStats(String playerName, LocalDate from, LocalDate to) {
        if (playerName == null || playerName.isBlank()) return null;

        try {
            List<MatchEntity> matches = matchRepository.findAll();
            final String target = playerName.trim();

            List<MatchEntity> playerMatches = matches.stream()
                    .filter(m -> m.getPlayerA().getName().equalsIgnoreCase(target)
                            || m.getPlayerB().getName().equalsIgnoreCase(target))
                    .filter(m ->
                            (from == null || !m.getDate().isBefore(from))
                                    && (to == null || !m.getDate().isAfter(to))
                    )
                    .toList();

            int total = playerMatches.size();
            int wins = 0;
            int losses = 0;

            for (MatchEntity m : playerMatches) {
                String score = m.getResult();
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