# 🎾 TennisApp (REST API version)

**Spring Boot REST API + CSV backend**  
Application for managing tennis players and matches.  
Originally a console project, now extended with a REST API layer.

---

## 🧩 Features
### 👥 Players
- `GET /api/players` – list all players  
- `POST /api/players` – create a new player (`@Valid` input)  
- JSON error handling (`ApiExceptionHandler`)  

### 🎾 Matches
- `GET /api/matches` – list matches  
- `POST /api/matches` – add a match with player validation  
- `PUT /api/matches` – update score or date  
- `DELETE /api/matches` – delete a match by parameters  
- `GET /api/matches/filter` – filter by player and/or date range

### 📊 Stats
- `GET /api/stats/player?name={name}&from=YYYY-MM-DD&to=YYYY-MM-DD` – player stats  
  - returns: total matches, wins, losses, **winRatePercent**  
- `GET /api/stats/leaderboard` – sorted leaderboard by win-rate  
- `GET /api/stats/export` – export leaderboard as CSV

Sample JSON for `/api/stats/player`:
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

## 🧱 Project structure
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

## ⚙️ Technologies
- ☕ **Java 23**
- 🚀 **Spring Boot 3.3.x**
- 🧩 **Maven**
- 🧰 **Jakarta Validation API**
- 💻 **IntelliJ IDEA**
- 🌐 **Git & GitHub**

---

## 🚀 How to run
### 1️⃣ Clone
```bash
git clone https://github.com/PeterPcolinsky/TennisApp.git
```

### 2️⃣ Run with Maven
```bash
mvn spring-boot:run
```
App runs on **http://localhost:8080**

### 3️⃣ Try the API (e.g., Postman)
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

## 🧪 Testing & Code Quality

Unit and integration tests using **Spring Boot Test + MockMvc**.

### 🔹 Tested modules
| Module | Test class | Notes |
|--------|------------|-------|
| Players (`PlayerController`) | `PlayerControllerTest.java` | CRUD + negative cases |
| Matches (`MatchController`) | `MatchControllerTest.java` | CRUD + filter endpoint |
| Stats (`StatsController`) | `StatsControllerTest.java` | player stats (range), leaderboard, export |

**Total: 19 tests – all passing ✅ (`BUILD SUCCESS`)**

### 🔹 Run tests
```bash
mvn test
```

---

## 🧰 DTOs & Services
- **DTOs:** `PlayerDto`, `PlayerStatsDto`, `MatchDto`, `MatchUpdateDto`, `LeaderboardDto`  
- **Services:** `CsvService`, `PlayerService`, `MatchService`, `StatsService`  
  - `StatsService#getLeaderboard()` – sorted leaderboard calculation

---

## 🗺️ Roadmap / Status
```
✅ Phase 1 – REST API (CSV backend) – completed
🚧 Phase 2 – JPA + Hibernate – in progress (CSV → DB migration)
⏳ Phase 3 – React Frontend – planned
```

---

## 👨‍💻 Author
**Peter Pčolinský**  
📍 Slovakia  
🎯 Goal: become a **Junior Java Developer in 2026**  
🔗 GitHub: https://github.com/PeterPcolinsky

---

**🇸🇰 [Slovak version →](README.md)**
