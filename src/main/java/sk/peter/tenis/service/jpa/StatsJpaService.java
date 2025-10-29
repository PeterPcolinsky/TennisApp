package sk.peter.tenis.service.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.repository.MatchRepository;
import sk.peter.tenis.repository.PlayerRepository;
import sk.peter.tenis.dto.PlayerStatsDto;
import sk.peter.tenis.dto.LeaderboardDto;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * JPA verzia štatistík – používa sa iba v profile "h2".
 * API (DTO) rovnaké.
 */
@Service
@Profile("h2")
public class StatsJpaService {

    private final MatchRepository matchRepo;
    private final PlayerRepository playerRepo;

    public StatsJpaService(MatchRepository matchRepo, PlayerRepository playerRepo) {
        this.matchRepo = matchRepo;
        this.playerRepo = playerRepo;
    }

    // ------------------- Public API -------------------

    public PlayerStatsDto getPlayerStats(String name, LocalDate from, LocalDate to) {
        String n = name == null ? null : name.trim();
        List<MatchEntity> matches = matchRepo.search(n, from, to);

        int wins = 0;
        int losses = 0;

        for (MatchEntity m : matches) {
            String winner = winnerName(m);
            if (winner == null) continue;

            if (n != null && n.equalsIgnoreCase(winner)) {
                wins++;
            } else if (n != null && (n.equalsIgnoreCase(m.getPlayerA().getName()) || n.equalsIgnoreCase(m.getPlayerB().getName()))) {
                losses++;
            }
        }

        int finished = wins + losses;
        double winRate = finished == 0 ? 0.0 : (wins * 100.0) / finished;

        PlayerStatsDto dto = new PlayerStatsDto();
        dto.setName(name);
        dto.setMatches(finished);
        dto.setWins(wins);
        dto.setLosses(losses);
        dto.setWinRatePercent(round1(winRate));
        return dto;
    }

    public List<LeaderboardDto> getLeaderboard() {
        // Všetky zápasy – spočítame štatistiky pre každého hráča
        List<MatchEntity> all = matchRepo.findAll();

        Map<String, int[]> map = new HashMap<>(); // name -> [wins, losses]

        for (MatchEntity m : all) {
            String a = m.getPlayerA().getName();
            String b = m.getPlayerB().getName();
            String w = winnerName(m);

            map.putIfAbsent(a, new int[]{0, 0});
            map.putIfAbsent(b, new int[]{0, 0});

            if (w == null) continue;
            if (w.equalsIgnoreCase(a)) {
                map.get(a)[0]++; // a wins
                map.get(b)[1]++; // b loses
            } else if (w.equalsIgnoreCase(b)) {
                map.get(b)[0]++;
                map.get(a)[1]++;
            }
        }

        List<LeaderboardDto> list = new ArrayList<>();
        for (var e : map.entrySet()) {
            String name = e.getKey();
            int wins = e.getValue()[0];
            int losses = e.getValue()[1];
            int played = wins + losses;
            double rate = played == 0 ? 0.0 : (wins * 100.0) / played;

            LeaderboardDto row = new LeaderboardDto(name, played, wins, losses, round1(rate));
            list.add(row);
        }

        // zoradené podľa winRate, potom podľa počtu zápasov
        return list.stream()
                .sorted(Comparator
                        .comparing(LeaderboardDto::getWinRatePercent).reversed()
                        .thenComparing(LeaderboardDto::getMatches).reversed()
                        .thenComparing(LeaderboardDto::getName))
                .collect(Collectors.toList());
    }

    public String exportLeaderboardCsv() {
        List<LeaderboardDto> lb = getLeaderboard();
        StringBuilder sb = new StringBuilder();
        sb.append("Name;Matches;Wins;Losses;WinRatePercent\n");
        for (LeaderboardDto r : lb) {
            sb.append(r.getName()).append(';')
                    .append(r.getMatches()).append(';')
                    .append(r.getWins()).append(';')
                    .append(r.getLosses()).append(';')
                    .append(String.format(Locale.US, "%.1f", r.getWinRatePercent()))
                    .append('\n');
        }
        return sb.toString();
    }

    // ------------------- Helpers -------------------

    // robustné určenie víťaza zo stringu "6:4, 6:2" atď.
    private String winnerName(MatchEntity m) {
        int setsA = 0, setsB = 0;
        String res = m.getResult();
        if (res == null || res.isBlank()) return null;

        String[] sets = res.split(",");
        for (String s : sets) {
            String[] ab = s.trim().split(":");
            if (ab.length != 2) continue;
            try {
                int a = Integer.parseInt(ab[0].trim());
                int b = Integer.parseInt(ab[1].trim());
                if (a > b) setsA++;
                else if (b > a) setsB++;
            } catch (NumberFormatException ignored) {
            }
        }
        if (setsA == setsB) return null;
        return setsA > setsB ? m.getPlayerA().getName() : m.getPlayerB().getName();
    }

    private double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}