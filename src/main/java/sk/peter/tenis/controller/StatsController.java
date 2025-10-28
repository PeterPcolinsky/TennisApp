package sk.peter.tenis.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.dto.LeaderboardDto;
import sk.peter.tenis.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * 🏆 Leaderboard zoradený podľa win-rate (desc).
     * Príklad: GET /api/stats/leaderboard
     */
    @GetMapping("/leaderboard")
    public List<LeaderboardDto> leaderboard() {
        return statsService.getLeaderboard();
    }

    /**
     * 📤 Export leaderboardu do CSV (položky oddelené bodkočiarkou).
     * Príklad: GET /api/stats/export
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        List<LeaderboardDto> data = statsService.getLeaderboard();

        StringBuilder sb = new StringBuilder();
        sb.append("Meno;Zapasy;Vyhry;Prehry;WinRate(%)\n");
        for (LeaderboardDto row : data) {
            sb.append(row.getName()).append(";")
                    .append(row.getMatches()).append(";")
                    .append(row.getWins()).append(";")
                    .append(row.getLosses()).append(";")
                    .append(String.format(java.util.Locale.US, "%.1f", row.getWinRatePercent()))
                    .append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=leaderboard.csv");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/top")
    public List<LeaderboardDto> top(
            @RequestParam(defaultValue = "3") int limit
    ) {
        int n = Math.max(1, Math.min(limit, 100)); // strážime rozsah
        return statsService.getLeaderboard()
                .stream()
                .limit(n)
                .toList();
    }

    @GetMapping("/player")
    public LeaderboardDto playerStats(
            @RequestParam String name,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        LeaderboardDto dto = statsService.getPlayerStats(name, from, to);
        if (dto == null) throw new IllegalArgumentException("Invalid player or parameters");
        return dto;
    }
}