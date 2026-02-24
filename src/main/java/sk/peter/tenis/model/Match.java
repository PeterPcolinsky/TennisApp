package sk.peter.tenis.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Tenisový zápas: hráč A, hráč B, textové skóre (sety) a dátum.
 */
public class Match {
    private Player playerA;
    private Player playerB;
    private String score;
    private LocalDate date;

    public Match(Player playerA, Player playerB, String score, LocalDate date) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.score = score;
        this.date = date;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public String getScore() {
        return score;
    }

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