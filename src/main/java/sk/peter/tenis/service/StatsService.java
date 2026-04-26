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
 * Service for computing player statistics and leaderboard from the database (H2/MySQL).
 * <p>
 * This service is active only for {@code h2} and {@code mysql} profiles and uses repositories
 * to read matches and players.
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

    public PlayerStatsDto getStatsForPlayer(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name must not be empty.");
        }

        final String target = playerName.trim().toLowerCase(Locale.ROOT);

        List<MatchEntity> allMatches = matchRepository.findAll();

        List<MatchEntity> playerMatches = allMatches.stream()
                .filter(m -> involves(m, target))
                .toList();

        int total = playerMatches.size();

        int[] wl = calculateWinsAndLosses(playerMatches, target);
        int wins = wl[0];
        int losses = wl[1];

        double winRate = calcWinRate(wins, losses);
        return new PlayerStatsDto(playerName, total, wins, losses, winRate);
    }

    private boolean involves(MatchEntity m, String target) {
        return m.getPlayerA().getName().equalsIgnoreCase(target)
                || m.getPlayerB().getName().equalsIgnoreCase(target);
    }

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
                .filter(m -> involves(m, name))
                .toList();

        int total = playerMatches.size();

        int[] wl = calculateWinsAndLosses(playerMatches, lowerName);
        int wins = wl[0];
        int losses = wl[1];

        double winRate = calcWinRate(wins, losses);
        return new LeaderboardDto(name, total, wins, losses, winRate);
    }

    public PlayerStatsDto getPlayerStats(String playerName, LocalDate from, LocalDate to) {
        if (playerName == null || playerName.isBlank()) return null;

        try {
            List<MatchEntity> matches = matchRepository.findAll();
            final String target = playerName.trim().toLowerCase(Locale.ROOT);

            List<MatchEntity> playerMatches = matches.stream()
                    .filter(m -> involves(m, target))
                    .filter(m ->
                            (from == null || !m.getDate().isBefore(from))
                                    && (to == null || !m.getDate().isAfter(to))
                    )
                    .toList();

            int total = playerMatches.size();

            int[] wl = calculateWinsAndLosses(playerMatches, target);
            int wins = wl[0];
            int losses = wl[1];

            double winRate = calcWinRate(wins, losses);
            return new PlayerStatsDto(playerName.trim(), total, wins, losses, winRate);

        } catch (Exception e) {
            return null;
        }
    }

    public double calcWinRate(int wins, int losses) {
        int finished = wins + losses;
        if (finished == 0) return 0.0;

        double raw = (wins * 100.0) / finished;
        return Math.round(raw * 10.0) / 10.0;
    }

    private int[] calculateWinsAndLosses(List<MatchEntity> matches, String targetLower) {
        int wins = 0;
        int losses = 0;

        for (MatchEntity m : matches) {
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

            if (winner.equals(targetLower)) wins++;
            else losses++;
        }

        return new int[]{wins, losses};
    }
}