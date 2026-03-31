package sk.peter.tenis.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a tennis match response.
 *
 * <p>Contains match details returned to the client,
 * including player names, score and match date.</p>
 */
public class MatchResponseDto {

    private final Long id;
    private final String playerAName;
    private final String playerBName;
    private final String score;
    private final LocalDate date;

    /**
     * Creates a match response DTO.
     *
     * @param id match ID
     * @param playerAName name of player A
     * @param playerBName name of player B
     * @param score match score (e.g. "6:4, 6:2")
     * @param date match date
     */
    public MatchResponseDto(Long id, String playerAName, String playerBName, String score, LocalDate date) {
        this.id = id;
        this.playerAName = playerAName;
        this.playerBName = playerBName;
        this.score = score;
        this.date = date;
    }

    /**
     * @return match ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @return name of player A
     */
    public String getPlayerAName() {
        return playerAName;
    }

    /**
     * @return name of player B
     */
    public String getPlayerBName() {
        return playerBName;
    }

    /**
     * @return match score
     */
    public String getScore() {
        return score;
    }

    /**
     * @return match date
     */
    public LocalDate getDate() {
        return date;
    }
}