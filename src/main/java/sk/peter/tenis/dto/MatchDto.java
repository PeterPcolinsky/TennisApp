package sk.peter.tenis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object used for creating and validating tennis matches
 * via REST API.
 * <p>
 * Expected CSV format: {@code PlayerA;PlayerB;Score;Date}
 * <ul>
 *     <li>Date must be in format {@code YYYY-MM-DD} (e.g. 2025-10-19)</li>
 *     <li>Score is provided as text (e.g. "6:4, 3:6, 7:5")</li>
 * </ul>
 */
public class MatchDto {

    @NotBlank(message = "Meno hráča A je povinné")
    private String playerA;

    @NotBlank(message = "Meno hráča B je povinné")
    private String playerB;

    @NotBlank(message = "Skóre je povinné (napr. \"6:4, 3:6, 7:5\")")
    @Pattern(
            regexp = "\\d{1,2}:\\d{1,2}(,\\s*\\d{1,2}:\\d{1,2})*",
            message = "Skóre musí byť vo formáte napr. \"6:4\" alebo \"6:4, 7:6\""
    )
    private String score;

    @NotBlank(message = "Dátum je povinný")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Dátum musí byť vo formáte YYYY-MM-DD")
    private String date;

    // getters & setters
    public String getPlayerA() {
        return playerA;
    }

    public void setPlayerA(String playerA) {
        this.playerA = playerA;
    }

    public String getPlayerB() {
        return playerB;
    }

    public void setPlayerB(String playerB) {
        this.playerB = playerB;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}