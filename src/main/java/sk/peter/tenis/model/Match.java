package sk.peter.tenis.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a tennis match between two players.
 * <p>
 * Contains players, match score (as text) and match date.
 */
public class Match {
    private Player playerA;
    private Player playerB;
    private String score;
    private LocalDate date;

    /**
     * Creates a new match.
     *
     * @param playerA first player
     * @param playerB second player
     * @param score   match result (e.g. "6:4, 6:3")
     * @param date    date of the match
     */
    public Match(Player playerA, Player playerB, String score, LocalDate date) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.score = score;
        this.date = date;
    }

    /**
     * @return first player
     */
    public Player getPlayerA() {
        return playerA;
    }

    /**
     * @return second player
     */
    public Player getPlayerB() {
        return playerB;
    }

    /**
     * @return match score as text
     */
    public String getScore() {
        return score;
    }

    /**
     * @return date of the match
     */
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Zápas: " + playerA.getName() + " vs " + playerB.getName() +
                " | Výsledok: " + score +
                " | Dátum: " + date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(playerA, match.playerA) &&
                Objects.equals(playerB, match.playerB) &&
                Objects.equals(score, match.score) &&
                Objects.equals(date, match.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerA, playerB, score, date);
    }
}