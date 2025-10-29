package sk.peter.tenis.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import sk.peter.tenis.entity.MatchEntity;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.repository.MatchRepository;
import sk.peter.tenis.repository.PlayerRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Jednorazový import zápasov z CSV do DB (iba profil 'h2').
 * Import prebehne, len ak je tabuľka MATCHES prázdna.
 * <p>
 * Očakávaný formát riadku:
 * HracA;HracB;Vysledok;Datum
 * Napr.:
 * Peter;Novak;"6:4, 6:2";2025-04-15
 * <p>
 * Delimiter je ';', whitespace sa orezáva.
 */
@Configuration
@Profile("h2")
public class MatchesSeeder {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    @Value("${tenis.csv.matches:./data/matches.csv}")
    private String matchesCsvPath;

    public MatchesSeeder(MatchRepository matchRepository,
                         PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
    }

    @PostConstruct
    public void seed() throws IOException {
        if (matchRepository.count() > 0) {
            return; // už sú zápasy v DB -> neseedujeme
        }

        Path path = Path.of(matchesCsvPath);
        if (!Files.exists(path)) {
            System.out.println("⚠ matches.csv nenájdený na ceste: " + path.toAbsolutePath());
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean headerProcessed = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // preskoč hlavičku, ak tam je
                if (!headerProcessed) {
                    String lower = line.toLowerCase();
                    if (lower.contains("hrac") || lower.contains("hráč") || lower.contains("vysledok") || lower.contains("výsledok") || lower.contains("datum") || lower.contains("dátum")) {
                        headerProcessed = true;
                        continue;
                    }
                    // ak prvý riadok NIE je hlavička, ideme ďalej normálne
                    headerProcessed = true;
                }

                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                String nameA = parts[0].trim();
                String nameB = parts[1].trim();
                String result = parts[2].trim().replace("\"", "");
                String dateStr = parts[3].trim();

                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr); // očakávame ISO formát YYYY-MM-DD
                } catch (Exception ex) {
                    // ak by si mal iný formát (napr. DD.MM.YYYY), uprav tento parsing
                    continue;
                }

                // nájdi hráčov podľa mena
                PlayerEntity playerA = playerRepository.findByNameIgnoreCase(nameA).orElse(null);
                PlayerEntity playerB = playerRepository.findByNameIgnoreCase(nameB).orElse(null);
                if (playerA == null || playerB == null) {
                    // hráč neexistuje v DB -> preskoč riadok (alebo zaloguj)
                    System.out.println("⚠ Preskakujem zápas: neznámy hráč A/B (" + nameA + "/" + nameB + ")");
                    continue;
                }

                // vyhnúť sa duplicitám (A vs B v ľubovoľnom poradí v ten istý dátum)
                boolean dup = matchRepository
                        .existsByPlayerA_NameIgnoreCaseAndPlayerB_NameIgnoreCaseAndDate(nameA, nameB, date)
                        || matchRepository
                        .existsByPlayerB_NameIgnoreCaseAndPlayerA_NameIgnoreCaseAndDate(nameA, nameB, date);
                if (dup) {
                    continue;
                }

                MatchEntity entity = new MatchEntity(playerA, playerB, result, date);
                matchRepository.save(entity);
            }
        }
    }
}