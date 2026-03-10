package sk.peter.tenis.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sk.peter.tenis.dto.LeaderboardDto;
import sk.peter.tenis.dto.PlayerStatsDto;
import sk.peter.tenis.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * REST controller for statistics and leaderboard endpoints.
 *
 * Provides endpoints for:
 * - leaderboard
 * - top players
 * - export leaderboard as CSV
 * - player stats for a selected date range
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private static final String CSV_HEADER = "Meno;Zapasy;Vyhry;Prehry;WinRate(%)\n";

    private static final Comparator<LeaderboardDto> WIN_RATE_DESC =
            Comparator.comparingDouble(LeaderboardDto::getWinRatePercent).reversed();

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    private List<LeaderboardDto> getLeaderboard() {
        return statsService.getLeaderboard();
    }

    private LocalDate parseDate(String value) {
        return (value != null && !value.isBlank()) ? LocalDate.parse(value) : null;
    }

    /**
     * Returns full leaderboard.
     */
    @GetMapping("/leaderboard")
    public List<LeaderboardDto> leaderboard() {
        return getLeaderboard();
    }

    /**
     * Returns top players by win rate.
     */
    @GetMapping("/top")
    public List<LeaderboardDto> getTopPlayers(@RequestParam(defaultValue = "3") int limit) {

        limit = Math.min(limit, 50);

        return getLeaderboard().stream()
                .filter(p -> p.getMatches() > 0 && p.getWinRatePercent() > 0)
                .sorted(WIN_RATE_DESC)
                .limit(limit)
                .toList();
    }

    /**
     * Exports the leaderboard as CSV file.
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        StringBuilder sb = new StringBuilder(256);
        sb.append(CSV_HEADER);

        for (LeaderboardDto row : getLeaderboard()) {
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
     * Returns player statistics with optional date range.
     */
    @GetMapping("/player")
    public PlayerStatsDto getPlayerStats(
            @RequestParam String name,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);

        return statsService.getPlayerStats(name, fromDate, toDate);
    }
}