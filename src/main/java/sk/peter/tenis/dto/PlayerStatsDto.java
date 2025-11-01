package sk.peter.tenis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerStatsDto {

    /**
     * Meno hráča (napr. "Novak Djokovic")
     */
    private String name;

    /**
     * Celkový počet odohraných zápasov
     */
    private int matches;

    /**
     * Počet výhier
     */
    private int wins;

    /**
     * Počet prehier
     */
    private int losses;

    /**
     * Percentuálna úspešnosť (0–100), zaokrúhlená v logike služby.
     * Príklad: 66.7 pre 2/3 výhier.
     */
    private double winRatePercent;

    // --- Konštruktory ---
    public PlayerStatsDto() {
    }

    public PlayerStatsDto(String name, int matches, int wins, int losses, double winRatePercent) {
        this.name = name;
        this.matches = matches;
        this.wins = wins;
        this.losses = losses;
        this.winRatePercent = winRatePercent;
    }

    // --- Gettre a Settre ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public double getWinRatePercent() {
        return winRatePercent;
    }

    public void setWinRatePercent(double winRatePercent) {
        this.winRatePercent = winRatePercent;
    }
}