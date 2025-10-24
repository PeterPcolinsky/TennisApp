package sk.peter.tenis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}