package sk.peter.tenis.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import sk.peter.tenis.entity.PlayerEntity;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.repository.PlayerRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Data seeder for loading initial player data from CSV into the database.
 *
 * Active only under the "h2" profile and runs once on application startup.
 * Seeding is performed only if the "players" table is empty.
 *
 * Reads player data from a CSV file defined in application properties.
 * Expected format: name;age;type (e.g. "Peter;30;Amatér").
 *
 * Key behavior:
 * - Skips invalid or malformed rows
 * - Ignores duplicate players (case-insensitive by name)
 * - Supports basic fallback parsing for player type
 *
 * Used only for development/testing with H2 database.
 */
@Configuration
@Profile("h2")
public class DataSeeder {

    private final PlayerRepository playerRepository;

    @Value("${tenis.csv.players:./data/players.csv}")
    private String playersCsvPath;

    public DataSeeder(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    @PostConstruct
    public void seed() throws IOException {
        if (playerRepository.count() > 0) {
            return;
        }

        Path path = Path.of(playersCsvPath);
        if (!Files.exists(path)) {
            System.out.println("⚠ players.csv nenájdený na ceste: " + path.toAbsolutePath());
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                if (parts.length < 3) continue;

                String name = parts[0].trim();

                int age;
                try {
                    age = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException ex) {
                    continue;
                }

                PlayerType type = PlayerType.fromInput(parts[2].trim());
                if (type == null) {
                    String t = parts[2].trim().toLowerCase(Locale.ROOT);
                    if (t.startsWith("amat")) type = PlayerType.AMATER;
                    else if (t.startsWith("prof")) type = PlayerType.PROFESIONAL;
                    else continue;
                }

                if (playerRepository.existsByNameIgnoreCase(name)) continue;

                PlayerEntity entity = new PlayerEntity(name, age, type);
                playerRepository.save(entity);
            }
        }
    }
}