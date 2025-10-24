# 🎾 TennisApp (REST API version)

**Spring Boot REST API + CSV backend**  
Application for managing tennis club players and matches.  
Originally a console project, now extended into a REST API.

---

## 🧩 Features
### 👥 Players
- `GET /api/players` – returns all players  
- `POST /api/players` – adds a new player  
- Input validation using `@Valid`  
- Error handling with JSON responses (`ApiExceptionHandler`)  
- Data persistence in `data/players.csv`

### 🎾 Matches
- `GET /api/matches` – list matches  
- `POST /api/matches` – add new match with player validation  
- `PUT /api/matches` – update match score or date  
- `DELETE /api/matches` – delete a match by parameters

---

## 🧱 Project structure
```
src/
 └── main/
     ├── java/sk/peter/tenis/
     │    ├── controller/      → REST controllers
     │    ├── dto/             → Data Transfer Objects (PlayerDto, MatchDto)
     │    ├── exception/       → ApiExceptionHandler
     │    ├── model/           → Player, PlayerType, Match
     │    ├── service/         → CsvService, PlayerService, MatchService, StatsService
     │    └── TenisApiApplication.java → Spring Boot main class
     └── resources/
          ├── application.properties → CSV path configuration
          └── data/players.csv       → player data storage
```

---

## ⚙️ Technologies used
- ☕ **Java 23**
- 🚀 **Spring Boot 3.3.x**
- 🧩 **Maven**
- 🧰 **Jakarta Validation API**
- 💻 **IntelliJ IDEA**
- 🌐 **Git & GitHub**

---

## 🚀 How to run
### 1️⃣ Clone repository
```bash
git clone https://github.com/PeterPcolinsky/TennisApp.git
```

### 2️⃣ Run with Maven
```bash
mvn spring-boot:run
```

The app runs at **http://localhost:8080**

### 3️⃣ Test API (e.g., Postman)
#### GET
```
GET http://localhost:8080/api/players
```
#### POST
```json
POST http://localhost:8080/api/players
{
  "name": "Novak Djokovic",
  "age": 37,
  "type": "PROFESIONAL"
}
```

---

## 🧪 Testing & Code Quality

The project includes unit and integration tests using **Spring Boot Test + MockMvc**.

### 🔹 Tested modules
| Module | Test class | Tests | Coverage |
|---------|-------------|--------|-----------|
| Players (`PlayerController`) | `PlayerControllerTest.java` | 7 | ✅ CRUD + stats + negative cases |
| Matches (`MatchController`) | `MatchControllerTest.java` | 5 | ✅ CRUD + negative cases |

### 🔹 Test types
- **Positive scenarios:** create, update, delete, and list players & matches  
- **Negative scenarios:** invalid input, missing players or matches  
- **Player statistics:** verified correct win/loss calculation from CSV data

### 🔹 Run tests
```bash
mvn test
```

All tests pass ✅  
Result: `BUILD SUCCESS`

---

## 🧰 Test tools
- **JUnit 5**
- **Spring Boot Starter Test**
- **MockMvc**
- **Hamcrest matchers**

---

## 🧠 Project goal
This project is part of a personal Java learning roadmap *(August – December 2025)*.  
Goal: build a complete backend REST API with validation and CSV persistence.  
Next planned extensions:
- Update & delete players (PUT, DELETE)  
- Database layer (MySQL, Hibernate)  
- Frontend (React)

---

## 👨‍💻 Author
**Peter Pčolinský**  
📍 Slovakia  
🎯 Goal: become a **Junior Java Developer in 2026**  
🔗 [GitHub – PeterPcolinsky](https://github.com/PeterPcolinsky)

---

**🇸🇰 [Slovak version →](README.md)**
