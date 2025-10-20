# 🎾 TennisApp (REST API version)

**Spring Boot REST API + CSV backend**  
Application for managing tennis club players.  
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
*(coming soon)*  
- `GET /api/matches` – list matches  
- `POST /api/matches` – add new match with player validation

---

## 🧱 Project structure
```
src/
 └── main/
     ├── java/sk/peter/tenis/
     │    ├── controller/      → REST controllers
     │    ├── dto/             → Data Transfer Objects (PlayerDto)
     │    ├── exception/       → ApiExceptionHandler
     │    ├── model/           → Player, PlayerType, Match
     │    ├── service/         → CsvService, PlayerService
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

## 🧠 Project goal
This project is part of a personal Java learning roadmap *(August – December 2025)*.  
Goal: build a complete backend REST API with validation and CSV persistence.  
Next planned extensions:
- Update & delete players (PUT, DELETE)  
- Manage matches (GET/POST `/api/matches`)  
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
