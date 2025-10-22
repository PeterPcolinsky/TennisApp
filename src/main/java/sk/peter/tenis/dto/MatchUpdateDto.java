package sk.peter.tenis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO pre aktualizáciu zápasu: vieme zmeniť score a/alebo date.
 * Pôvodný zápas vyhľadáme podľa playerA, playerB, date, score (query parametre v URL).
 */
public class MatchUpdateDto {

    // nové hodnoty – aspoň jedno z nich musí byť neprázdne (zatiaľ nekontrolujeme „aspoň jedno“)
    private String newScore;

    @Pattern(regexp = "^$|\\d{4}-\\d{2}-\\d{2}", message = "Dátum musí byť vo formáte YYYY-MM-DD")
    private String newDate;

    public String getNewScore() { return newScore; }
    public void setNewScore(String newScore) { this.newScore = newScore; }

    public String getNewDate() { return newDate; }
    public void setNewDate(String newDate) { this.newDate = newDate; }
}