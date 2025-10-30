# 🎾 TennisApp – REST API for Managing Tennis Matches

## 🧩 Project Overview
TennisApp is a multi-layered Spring Boot application that manages tennis players, matches, and statistics.  
The project supports a **dual data mode**:
- **CSV mode (default)** – data is stored in local CSV files.
- **JPA mode (profile `h2`)** – data is stored in an embedded H2 database using Spring Data JPA and Hibernate.

---

## ⚙️ Technologies Used
- **Java 23 (OpenJDK)**
- **Spring Boot 3.3**
- **Spring Web / REST API**
- **Spring Data JPA + Hibernate**
- **H2 Database (profile `h2`)**
- **Lombok**
- **JUnit 5**
- **Postman (for endpoint testing)**
- **Maven**

---

## 🧱 Project Structure

```
tenis/
 ├── data/
 │   ├── players.csv
 │   ├── matches.csv
 │
 ├── src/main/java/sk/peter/tenis/
 │   ├── controller/
 │   │    ├── PlayerController.java
 │   │    ├── MatchController.java
 │   │    └── StatsController.java
 │   │
 │   ├── dto/
 │   │    ├── PlayerDto.java
 │   │    ├── MatchDto.java
 │   │    └── LeaderboardDto.java
 │   │
 │   ├── entity/
 │   │    ├── PlayerEntity.java
 │   │    └── MatchEntity.java
 │   │
 │   ├── repository/
 │   │    ├── PlayerRepository.java
 │   │    └── MatchRepository.java
 │   │
 │   ├── service/
 │   │    ├── PlayerService.java
 │   │    ├── MatchService.java
 │   │    ├── StatsService.java
 │   │    ├── CsvService.java
 │   │    └── jpa/
 │   │         ├── PlayerJpaService.java
 │   │         ├── MatchJpaService.java
 │   │         └── StatsJpaService.java
 │   │
 │   ├── exception/
 │   │    ├── NotFoundException.java
 │   │    └── ApiExceptionHandler.java
 │   │
 │   └── TenisApiApplication.java
 │
 ├── src/main/resources/
 │   ├── application.properties
 │   └── application-h2.properties
 │
 └── pom.xml
```

---

## 🧭 Development Phases

| Phase | Description |
|-------|--------------|
| **1** | Console application (CSV) |
| **2** | REST API – CSV mode |
| **3** | DTO and Controllers |
| **4** | Player statistics and leaderboard (CSV) |
| **5** | **JPA Integration (H2, Hibernate)** – PlayerEntity, MatchEntity, StatsJpaService |
| **6** | **CRUD for Players and Matches (JPA)** – PlayerJpaService, MatchJpaService, Dual Data Mode (CSV ↔ H2) |

---

## 🗄️ Application Modes

### 🔹 CSV Mode (default)
Data is read and written from/to `players.csv` and `matches.csv`.

### 🔹 JPA / H2 Mode
Data is stored in the H2 database (`./data/tenisdb.mv.db`).

#### Activation in IntelliJ IDEA
Go to **Run Configuration → Modify options → Add VM options** and add:
```
-Dspring.profiles.active=h2
```
Then check the console log:
```
The following 1 profile is active: "h2"
```

---

## 🚀 How to Run the Application

1. Import the project as a **Maven Project**.  
2. Run `TenisApiApplication.java`.  
3. Verify in logs whether the active profile is CSV or H2.  
4. Test the endpoints using **Postman**:

| Method | Endpoint | Description |
|---------|-----------|-------------|
| GET | `/api/players` | Retrieve all players |
| POST | `/api/players` | Add a new player |
| DELETE | `/api/players/{name}` | Delete a player |
| GET | `/api/matches` | Retrieve all matches |
| POST | `/api/matches` | Add a new match |
| DELETE | `/api/matches/{playerA}/{playerB}/{date}/{score}` | Delete a match |
| GET | `/api/stats/leaderboard` | Get player leaderboard |
| GET | `/api/stats/player?name=Peter` | Get stats for a specific player |
| GET | `/api/stats/export` | Export leaderboard as CSV |

---

## 🧪 Testing
All endpoints were tested using **Postman**:  
- CRUD operations for players and matches  
- Player and date filters  
- Statistics and CSV export  
✅ All requests returned `200 OK`.

---

## 🧩 Roadmap

- 🔜 **Phase 7** – Migrate from H2 to MySQL  
- 🔜 **Phase 8** – Unit testing and refactoring  
- 🔜 **Phase 9** – Frontend (React) for leaderboard and statistics  
- 🔜 **Phase 10** – Dockerization and CI/CD pipeline  

---

## 👤 Author
**Peter Pčolinský**  
GitHub: [PeterPcolinsky](https://github.com/PeterPcolinsky)
