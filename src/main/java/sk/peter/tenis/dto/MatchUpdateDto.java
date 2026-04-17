package sk.peter.tenis.dto;

import jakarta.validation.constraints.Pattern;

/**
 * DTO used for updating an existing match.
 *
 * Allows partial update of match data:
 * - newScore: updated match result (optional)
 * - newDate: updated match date in format YYYY-MM-DD (optional)
 *
 * The original match is identified by playerA, playerB, score and date
 * provided as query parameters in the request.
 */
public class MatchUpdateDto {

    // nové hodnoty – aspoň jedno z nich musí byť neprázdne (zatiaľ nekontrolujeme „aspoň jedno“)
    private String newScore;

    @Pattern(regexp = "^$|\\d{4}-\\d{2}-\\d{2}", message = "Dátum musí byť vo formáte YYYY-MM-DD")
    private String newDate;

    public String getNewScore() {
        return newScore;
    }

    public void setNewScore(String newScore) {
        this.newScore = newScore;
    }

    public String getNewDate() {
        return newDate;
    }

    public void setNewDate(String newDate) {
        this.newDate = newDate;
    }
}