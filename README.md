# 🎾 TenisApp (REST API verzia)

**Spring Boot REST API + CSV backend**  
Aplikácia pre evidenciu hráčov a zápasov tenisového klubu.  
Projekt pôvodne vznikol ako konzolová verzia a bol rozšírený o REST API vrstvu.

---

## 🧩 Funkcionality
### 👥 Hráči (Players)
- `GET /api/players` – zobrazí všetkých hráčov  
- `POST /api/players` – pridá nového hráča (validácia `@Valid`)  
- Chybové správy v JSON formáte (`ApiExceptionHandler`)  

### 🎾 Zápasy (Matches)
- `GET /api/matches` – načítanie zápasov  
- `POST /api/matches` – pridanie zápasu s overením hráčov  
- `PUT /api/matches` – aktualizácia výsledku alebo dátumu  
- `DELETE /api/matches` – zmazanie zápasu podľa parametrov  
- `GET /api/matches/filter` – filtrovanie podľa hráča a/alebo dátumového rozsahu

### 📊 Štatistiky (Stats)
- `GET /api/stats/player?name={meno}&from=YYYY-MM-DD&to=YYYY-MM-DD` – štatistiky hráča  
  - výsledok obsahuje: počet zápasov, výhry, prehry, **winRatePercent**  
- `GET /api/stats/leaderboard` – rebríček hráčov podľa win-rate (zoradené)  
- `GET /api/stats/export` – export rebríčka do CSV

Ukážka JSON pre `/api/stats/player`:
```json
{
  "name": "Peter",
  "matches": 2,
  "wins": 2,
  "losses": 0,
  "winRatePercent": 100.0
}
```

---

## 🧱 Štruktúra projektu
```
tenis/
 ├── data/
 │   ├── matches.csv
 │   └── players.csv
 └── src/
     ├── main/
     │   ├── java/sk/peter/tenis/
     │   │   ├── controller/      → HealthController, MatchController, PlayerController, StatsController
     │   │   ├── dto/             → PlayerDto, PlayerStatsDto, MatchDto, MatchUpdateDto, LeaderboardDto
     │   │   ├── exception/       → ApiExceptionHandler, NotFoundException
     │   │   ├── model/           → Player, PlayerType, Match
     │   │   ├── service/         → CsvService, PlayerService, MatchService, StatsService
     │   │   ├── ui/              → ConsoleApp
     │   │   ├── util/            → Printer
     │   │   ├── App.java
     │   │   └── TenisApiApplication.java
     │   └── resources/
     │       └── application.properties
     └── test/
         └── java/sk/peter/tenis/controller/
             ├── PlayerControllerTest.java
             ├── MatchControllerTest.java
             └── StatsControllerTest.java
```

---

## ⚙️ Použité technológie
- ☕ **Java 23**
- 🚀 **Spring Boot 3.3.x**
- 🧩 **Maven**
- 🧰 **Jakarta Validation API**
- 💻 **IntelliJ IDEA**
- 🌐 **Git & GitHub**

---

## 🚀 Spustenie projektu
### 1️⃣ Klonovanie
```bash
git clone https://github.com/PeterPcolinsky/TennisApp.git
```

### 2️⃣ Spustenie cez Maven
```bash
mvn spring-boot:run
```
Aplikácia beží na **http://localhost:8080**

### 3️⃣ Testovanie API (napr. Postman)
#### GET
```
GET http://localhost:8080/api/players
```
#### POST
```http
POST http://localhost:8080/api/players
Content-Type: application/json

{
  "name": "Novak",
  "age": 37,
  "type": "PROFESIONAL"
}
```

---

## 🧪 Testovanie a kvalita kódu

Projekt obsahuje jednotkové a integračné testy postavené na **Spring Boot Test + MockMvc**.

### 🔹 Testované moduly
| Modul | Súbor testov | Poznámka |
|-------|---------------|----------|
| Hráči (`PlayerController`) | `PlayerControllerTest.java` | CRUD + negatívne prípady |
| Zápasy (`MatchController`) | `MatchControllerTest.java` | CRUD + filter endpoint |
| Štatistiky (`StatsController`) | `StatsControllerTest.java` | player stats (range), leaderboard, export |

**Celkovo: 19 testov – všetky prechádzajú ✅ (`BUILD SUCCESS`)**

### 🔹 Spustenie testov
```bash
mvn test
```

---

## 🧰 DTO a služby
- **DTO:** `PlayerDto`, `PlayerStatsDto`, `MatchDto`, `MatchUpdateDto`, `LeaderboardDto`  
- **Služby:** `CsvService`, `PlayerService`, `MatchService`, `StatsService`  
  - `StatsService#getLeaderboard()` – výpočet zoradeného rebríčka

---

## 🗺️ Fázy a stav projektu
```
✅ Fáza 1 – REST API (CSV backend) – dokončené
🚧 Fáza 2 – JPA + Hibernate – pripravované (migrácia z CSV na DB)
⏳ Fáza 3 – React Frontend – plánované
```

---

## 👨‍💻 Autor
**Peter Pčolinský**  
📍 Slovensko  
🎯 Cieľ: stať sa **Junior Java Developerom v roku 2026**  
🔗 GitHub: https://github.com/PeterPcolinsky

---

**🇬🇧 [English version →](README_EN.md)**
