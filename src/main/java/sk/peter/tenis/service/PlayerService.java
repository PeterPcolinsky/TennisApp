package sk.peter.tenis.service;

import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.PlayerDto;
import sk.peter.tenis.exception.NotFoundException;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.model.PlayerType;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerService {

    public List<Player> findAll() {
        List<Player> result = new ArrayList<>();
        try {
            CsvService.loadPlayers(result);
        } catch (Exception e) {
            // CSV fáza: ticho vrátime to, čo je
        }
        return result;
    }

    public Player createFromDto(PlayerDto dto) {
        String name = dto.getName().trim();

        // 1) namapuj typ – skús najprv tvoju utilitu, potom názov enumu
        PlayerType type = PlayerType.fromInput(dto.getType());
        if (type == null) {
            try {
                type = PlayerType.valueOf(dto.getType().trim().toUpperCase());
            } catch (Exception ignored) {
                type = PlayerType.AMATER; // fallback
            }
        }

        // 2) načítaj existujúcich hráčov
        List<Player> players = new ArrayList<>();
        try {
            CsvService.loadPlayers(players);
        } catch (Exception e) {
            // ak sa nepodarí načítať, pokračujeme s prázdnym zoznamom
        }

        // 3) kontrola duplicitného mena (case-insensitive, celé meno)
        boolean exists = players.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name));

        if (exists) {
            throw new IllegalArgumentException(
                    "Hráč s týmto menom už existuje. Zadaj prosím celé meno (meno + priezvisko), aby sme ich vedeli odlíšiť."
            );
        }

        // 4) vytvor Player cez KONŠTRUKTOR (tvoj poriadok je name, age, type)
        Player p = new Player(name, dto.getAge(), type);

        // 5) pridaj a ulož
        players.add(p);
        try {
            CsvService.savePlayers(players);
        } catch (Exception e) {
            // CSV režim – ak sa nepodarí uložiť, nezhodíme API
        }
        return p;
    }

    public Player update(String name, PlayerDto dto) {
        List<Player> players = new ArrayList<>();
        try {
            CsvService.loadPlayers(players);

            // nájdi hráča podľa mena (case-insensitive)
            int idx = -1;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getName().equalsIgnoreCase(name)) {
                    idx = i; break;
                }
            }
            if (idx < 0) throw new NotFoundException("Player not found");

            // mapuj typ
            PlayerType type = PlayerType.fromInput(dto.getType());
            if (type == null) {
                try { type = PlayerType.valueOf(dto.getType().trim().toUpperCase()); }
                catch (Exception ignored) { type = PlayerType.AMATER; }
            }

            // meno ponecháme pôvodné (renaming riešime neskôr), update len age + type
            Player original = players.get(idx);
            Player updated = new Player(original.getName(), dto.getAge(), type);
            players.set(idx, updated);

            CsvService.savePlayers(players);
            return updated;
        } catch (NotFoundException nf) {
            throw nf;
        } catch (Exception e) {
            // jednoduché CSV spracovanie – vrátime pôvodný stav
            throw new RuntimeException("Unable to update player");
        }
    }

    public void delete(String name) {
        List<Player> players = new ArrayList<>();
        try {
            CsvService.loadPlayers(players);
            boolean removed = players.removeIf(p -> p.getName().equalsIgnoreCase(name));
            if (!removed) throw new NotFoundException("Player not found");
            CsvService.savePlayers(players);
        } catch (NotFoundException nf) {
            throw nf;
        } catch (Exception e) {
            throw new RuntimeException("Unable to delete player");
        }
    }
}