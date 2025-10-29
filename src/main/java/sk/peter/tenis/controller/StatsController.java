package sk.peter.tenis.controller;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.dto.LeaderboardDto;
import sk.peter.tenis.dto.PlayerStatsDto;
import sk.peter.tenis.service.StatsService;
import sk.peter.tenis.service.jpa.StatsJpaService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * REST controller pre štatistiky (CSV + JPA režim).
 * Automaticky prepína zdroj dát podľa aktívneho Spring profilu.
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService csvStats;
    private final StatsJpaService jpaStats;
    private final Environment env;

    public StatsController(StatsService csvStats,
                           StatsJpaService jpaStats,
                           Environment env) {
        this.csvStats = csvStats;
        this.jpaStats = jpaStats;
        this.env = env;
    }

    // ----------------- Pomocné -----------------

    private boolean isJpaActive() {
        return Arrays.stream(env.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("h2"));
    }

    // ----------------- Endpointy -----------------

    /**
     * 🏆 Leaderboard zoradený podľa win-rate (desc).
     * Príklad: GET /api/stats/leaderboard
     */
    @GetMapping("/leaderboard")
    public List<LeaderboardDto> leaderboard() {
        if (isJpaActive()) {
            return jpaStats.getLeaderboard();
        }
        return csvStats.getLeaderboard();
    }

    /**
     * 📤 Export leaderboardu do CSV (položky oddelené bodkočiarkou).
     * Príklad: GET /api/stats/export
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        List<LeaderboardDto> data = isJpaActive()
                ? jpaStats.getLeaderboard()
                : csvStats.getLeaderboard();

        StringBuilder sb = new StringBuilder();
        sb.append("Meno;Zapasy;Vyhry;Prehry;WinRate(%)\n");
        for (LeaderboardDto row : data) {
            sb.append(row.getName()).append(";")
                    .append(row.getMatches()).append(";")
                    .append(row.getWins()).append(";")
                    .append(row.getLosses()).append(";")
                    .append(String.format(Locale.US, "%.1f", row.getWinRatePercent()))
                    .append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=leaderboard.csv");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /**
     * 📈 Top N hráčov podľa win-rate (desc)
     * Príklad: GET /api/stats/top?limit=3
     */
    @GetMapping("/top")
    public List<LeaderboardDto> top(@RequestParam(defaultValue = "3") int limit) {
        int n = Math.max(1, Math.min(limit, 100));
        List<LeaderboardDto> leaderboard = isJpaActive()
                ? jpaStats.getLeaderboard()
                : csvStats.getLeaderboard();

        return leaderboard.stream().limit(n).toList();
    }

    /**
     * 🎯 Štatistiky pre konkrétneho hráča v danom období.
     * Príklad: GET /api/stats/player?name=Peter&from=2025-04-01&to=2025-05-31
     */
    @GetMapping("/player")
    public Object playerStats(
            @RequestParam String name,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        if (isJpaActive()) {
            LocalDate fromDate = parseDateSafe(from);
            LocalDate toDate = parseDateSafe(to);
            PlayerStatsDto dto = jpaStats.getPlayerStats(name, fromDate, toDate);
            if (dto == null) throw new IllegalArgumentException("Invalid player or parameters");
            return dto;
        } else {
            LeaderboardDto dto = csvStats.getPlayerStats(name, from, to);
            if (dto == null) throw new IllegalArgumentException("Invalid player or parameters");
            return dto;
        }
    }

    // ----------------- Pomocné -----------------
    private LocalDate parseDateSafe(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception ignored) {
            return null;
        }
    }
}