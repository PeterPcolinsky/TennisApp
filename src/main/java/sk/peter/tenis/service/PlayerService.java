package sk.peter.tenis.service;

import org.springframework.stereotype.Service;
import sk.peter.tenis.dto.PlayerDto;
import sk.peter.tenis.model.Player;
import sk.peter.tenis.model.PlayerType;
import sk.peter.tenis.exception.NotFoundException;

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
        // 1) namapuj typ – skús najprv tvoju utilitu, potom názov enumu
        PlayerType type = PlayerType.fromInput(dto.getType());
        if (type == null) {
            try {
                type = PlayerType.valueOf(dto.getType().trim().toUpperCase());
            } catch (Exception ignored) {
                type = PlayerType.AMATER; // fallback
            }
        }

        // 2) vytvor Player cez KONŠTRUKTOR (tvoj poriadok je name, age, type)
        Player p = new Player(dto.getName().trim(), dto.getAge(), type);

        // 3) načítaj, pridaj, ulož
        List<Player> players = new ArrayList<>();
        try {
            CsvService.loadPlayers(players);
            players.add(p);
            CsvService.savePlayers(players);
        } catch (Exception ignored) {
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