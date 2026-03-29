package sk.peter.tenis.dto;

/**
 * Data Transfer Object representing a leaderboard row.
 *
 * <p>Contains aggregated statistics for a single player,
 * including number of matches, wins, losses and win rate percentage.</p>
 */
public class LeaderboardDto {
    private String name;
    private int matches;
    private int wins;
    private int losses;
    private double winRatePercent;

    /**
     * Creates a leaderboard entry.
     *
     * @param name player name
     * @param matches total number of matches played
     * @param wins number of wins
     * @param losses number of losses
     * @param winRatePercent win rate in percent
     */
    public LeaderboardDto(String name, int matches, int wins, int losses, double winRatePercent) {
        this.name = name;
        this.matches = matches;
        this.wins = wins;
        this.losses = losses;
        this.winRatePercent = winRatePercent;
    }

    /**
     * @return player name
     */
    public String getName() {
        return name;
    }

    /**
     * @return number of matches played
     */
    public int getMatches() {
        return matches;
    }

    /**
     * @return number of wins
     */
    public int getWins() {
        return wins;
    }

    /**
     * @return number of losses
     */
    public int getLosses() {
        return losses;
    }

    /**
     * @return win rate percentage
     */
    public double getWinRatePercent() {
        return winRatePercent;
    }

    @Override
    public String toString() {
        return String.format("%s (%d zápasov, %d výhier, %.1f%%)",
                name, matches, wins, winRatePercent);
    }
}