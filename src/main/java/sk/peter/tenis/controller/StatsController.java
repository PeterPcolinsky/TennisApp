package sk.peter.tenis.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.peter.tenis.dto.LeaderboardDto;
import sk.peter.tenis.dto.PlayerStatsDto;
import sk.peter.tenis.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardDto> leaderboard() {
        return statsService.getLeaderboard();
    }

    @GetMapping("/top")
    public List<LeaderboardDto> getTopPlayers(@RequestParam(defaultValue = "3") int limit) {
        return statsService.getLeaderboard().stream()
                .filter(p -> p.getMatches() > 0 && p.getWinRatePercent() > 0)
                .sorted(Comparator.comparingDouble(LeaderboardDto::getWinRatePercent).reversed())
                .limit(limit)
                .toList();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Meno;Zapasy;Vyhry;Prehry;WinRate(%)\n");
        for (LeaderboardDto row : statsService.getLeaderboard()) {
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

    @GetMapping("/player")
    public PlayerStatsDto getPlayerStats(
            @RequestParam String name,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        LocalDate fromDate = (from != null && !from.isBlank()) ? LocalDate.parse(from) : null;
        LocalDate toDate = (to != null && !to.isBlank()) ? LocalDate.parse(to) : null;

        return statsService.getPlayerStats(name, fromDate, toDate);
    }
}