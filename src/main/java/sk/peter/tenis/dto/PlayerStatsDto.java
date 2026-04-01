package sk.peter.tenis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing player statistics.
 *
 * <p>Contains aggregated data about a player such as number of matches,
 * wins, losses and win rate percentage.</p>
 *
 * <p>Null values are excluded from JSON response.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerStatsDto {

    /**
     * Player name (e.g. "Novak Djokovic")
     */
    private String name;

    /**
     * Total number of matches played
     */
    private int matches;

    /**
     * Number of wins
     */
    private int wins;

    /**
     * Number of losses
     */
    private int losses;

    /**
     * Win rate percentage (0–100), rounded in service logic.
     * Example: 66.7 for 2/3 wins.
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