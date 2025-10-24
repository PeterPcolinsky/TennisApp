package sk.peter.tenis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.PlayerStatsDto;
import sk.peter.tenis.model.Match;
import sk.peter.tenis.model.Player;

import java.util.List;
/**
 * Služba pre výpočet štatistík hráčov na základe CSV dát.
 */
@Service
@RequiredArgsConstructor
public class StatsService {

    private final MatchService matchService;

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
                } catch (NumberFormatException ignored) {}
            }

            if (setsA == 0 && setsB == 0) continue; // žiadny validný set

            String winner = setsA > setsB ? a.getName().toLowerCase() : b.getName().toLowerCase();

            if (winner.equals(target)) wins++;
            else losses++;
        }

        double winRate = total == 0 ? 0.0 : round((wins * 100.0) / total);

        return PlayerStatsDto.builder()
                .name(playerName)
                .matches(total)
                .wins(wins)
                .losses(losses)
                .winRatePercent(winRate)
                .build();
    }

    // Pomocné metódy
    private boolean involves(Match m, String target) {
        return m.getPlayerA().getName().equalsIgnoreCase(target)
                || m.getPlayerB().getName().equalsIgnoreCase(target);
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    // 👇 Pôvodná pomocná metóda z konzolovej verzie
    public double calcWinRate(int wins, int losses) {
        int finished = wins + losses;
        if (finished == 0) return 0.0;
        return (wins * 100.0) / finished;
    }
}