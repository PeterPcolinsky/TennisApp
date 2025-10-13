package sk.peter.tenis.model;

import java.time.LocalDate;

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
}
