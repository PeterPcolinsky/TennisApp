package sk.peter.tenis.dto;

public class LeaderboardDto {
    private String name;
    private int matches;
    private int wins;
    private int losses;
    private double winRatePercent;

    public LeaderboardDto(String name, int matches, int wins, int losses, double winRatePercent) {
        this.name = name;
        this.matches = matches;
        this.wins = wins;
        this.losses = losses;
        this.winRatePercent = winRatePercent;
    }

    // Gettre
    public String getName() {
        return name;
    }

    public int getMatches() {
        return matches;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public double getWinRatePercent() {
        return winRatePercent;
    }

    @Override
    public String toString() {
        return String.format("%s (%d zápasov, %d výhier, %.1f%%)",
                name, matches, wins, winRatePercent);
    }
}
