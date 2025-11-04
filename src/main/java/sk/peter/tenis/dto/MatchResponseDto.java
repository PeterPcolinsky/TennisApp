package sk.peter.tenis.dto;

import java.time.LocalDate;

public class MatchResponseDto {

    private Long id;
    private String playerAName;
    private String playerBName;
    private String score;
    private LocalDate date;

    public MatchResponseDto(Long id, String playerAName, String playerBName, String score, LocalDate date) {
        this.id = id;
        this.playerAName = playerAName;
        this.playerBName = playerBName;
        this.score = score;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getPlayerAName() {
        return playerAName;
    }

    public String getPlayerBName() {
        return playerBName;
    }

    public String getScore() {
        return score;
    }

    public LocalDate getDate() {
        return date;
    }
}