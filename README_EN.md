# 🎾 TennisApp – REST API (Spring Boot)

A complete Java (Spring Boot) application for managing tennis players and matches via REST API.  
Supports both **CSV** and **MySQL (JPA)** modes, allowing full CRUD operations.

---

## 🚀 Current Status

✅ Fully functional REST API  
✅ CRUD operations for players and matches  
✅ Player statistics (wins, losses, win rate)  
✅ Postman tests passed  
✅ MySQL migration (Phase 7) completed  
🔜 Phase 8 – Unit testing and refactor

---

## 🧩 Development Phases

| Phase | Description | Status |
|--------|--------------|--------|
| 1 | CSV loading and saving | ✅ Done |
| 2 | REST API for players and matches | ✅ Done |
| 3 | Player statistics | ✅ Done |
| 4 | DTO and validation | ✅ Done |
| 5 | Exception handling | ✅ Done |
| 6 | JPA integration (H2 DB) | ✅ Done |
| 7 | MySQL migration (JPA + DataSeeder) | ✅ Done |
| 8 | Unit tests & refactor | 🔜 Upcoming |
| 9 | React frontend (Leaderboard UI) | 🔜 Future |

---

## 🗂️ Project Structure

```
src/
 ├─ main/
 │   ├─ java/sk/peter/tenis/
 │   │   ├─ controller/
 │   │   ├─ dto/
 │   │   ├─ entity/
 │   │   ├─ exception/
 │   │   ├─ model/
 │   │   ├─ repository/
 │   │   ├─ service/
 │   │   │   ├─ jpa/
 │   │   │   └─ CsvService.java
 │   │   ├─ DataSeeder.java         ← NEW (imports CSV → MySQL at startup)
 │   │   └─ TenisApiApplication.java
 │   └─ resources/
 │       ├─ application.properties
 │       ├─ application-h2.properties
 │       └─ application-mysql.properties   ← NEW (MySQL configuration)
 ├─ test/
 │   ├─ PlayerControllerTest.java
 │   ├─ MatchControllerTest.java
 │   └─ StatsControllerTest.java
 └─ data/
     ├─ players.csv
     └─ matches.csv
```

---

## ⚙️ MySQL Profile

Run the app with the `mysql` profile to use a real MySQL database instead of H2.

### Example (application-mysql.properties)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tennisapp?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
logging.level.org.hibernate.SQL=DEBUG
```

---

## 🧠 Testing via Postman

| Operation | Endpoint | Description |
|------------|-----------|-------------|
| GET | `/api/players` | Returns all players |
| POST | `/api/players` | Adds a new player |
| PUT | `/api/players/{name}` | Updates player info |
| DELETE | `/api/players/{name}` | Deletes a player |
| GET | `/api/matches` | Returns all matches |
| POST | `/api/matches` | Adds a new match |
| PUT | `/api/matches/{id}` | Updates match by ID |
| DELETE | `/api/matches/{id}` | Deletes match by ID |

---

## 🧾 Database Integration

✅ Automatic CSV → MySQL import via `DataSeeder` at startup  
✅ Real tables `players` and `matches` (phpMyAdmin verified)  
✅ Full CRUD persistence through Hibernate JPA  
✅ Instant DB sync – no manual refresh required

---

## 📅 Next Step

➡️ **Phase 8 – Unit testing and refactor (Player, Match, Stats controllers)**  
➡️ **Phase 9 – React frontend (Leaderboard UI)**

---

**Author:** Peter Pčolinský  
**GitHub:** [PeterPcolinsky](https://github.com/PeterPcolinsky)
