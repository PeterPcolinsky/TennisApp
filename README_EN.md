# 🎾 TennisMate – Tennis Application (Spring Boot + MySQL + React)

TennisMate is a fullstack tennis management system combining:
- **Spring Boot (REST API)**
- **MySQL**
- **JPA / Hibernate**
- **React (Vite) frontend**
- **Security (Basic Auth: ADMIN / USER)**
- **CSV fallback mode**
- **Validation, stats, leaderboard, matches, players**

This project serves as a strong portfolio piece for HR and developers.

---

## 🚀 Main Features
- Login system (ADMIN / USER)
- CRUD for players
- CRUD for matches
- Automatic leaderboard based on match results
- Statistics (wins, losses, winrate)
- Backend + frontend validation
- Duplicate-name protection
- MySQL persistence with automatic CSV import
- Clean REST API with error handling

---

## 📦 Project Structure
### Backend (Spring Boot)
```
src/main/java/sk/peter/tenis/
│── config/CorsConfig, DataSeeder, MatchesSeeder, SecurityConfig.java
│── controller/HealthController, MatchController, PlayerController, StatsController
│── dto/LeaderboardDto, MatchDto,MatchResponseDto,MatchUpdateDto,PlayerDto,PlayerStatsDto
│── entity/MatchEntity, PlayerEntity
│── exception/ApiExpectationHandler, NotFoundException
│── model/Match, Player, PlayerType
│── repository/MatchRepository, PlayerRepository
│── service/CsvService, MatchService, PlayerService, StatsService
│── service/jpa/MatchJpaService, PlayerJpaService, StatsJpaService
│── ui/ConsoleApp
│── util/Printer
│── App.java
│── DataSeeder.java
│── TenisApiApplication.java
src/main/resources
│── static/assets, index.html, vite.svg
│── application.properties
│── application-h2.properties
│── aplication-mysql.properties
src/test/java/_archive
│── MatchControllerTest
src/test/java/sk/peter/tenis
│── annotations/TestWithoutSecurity
│── config/TestSecurityConfig
│── controller/MatchControllerCsvTest, PlayerControllerTest, StatsCOntrollerTest
│── Service/MatchJpaServiceTest, PlayerJpaServiceTest, StatsJpaServiceTest
```

### Frontend (React + Vite)
```
src/
│── assets/react.svg
│── components/AddMatchForm,jsx, AddPlayerForm.jsx, LeaderboardTable.jsx, LoginForm,jsx, MatchesTable.jsx, PlayersTable.jsx
│── services/api.js
│── App.css
│── App.jsx
│── index.css
│── main.jsx
```

---

## 🛑 Security & Authentication
- ADMIN: `admin / admin911!`
- USER: `user / user`
- ADMIN permissions:
  - add players
  - delete players
  - add matches
  - delete matches

Security includes:
- custom authentication entry point  
- removed browser login popup  
- protected `/api/**` endpoints  

---

## 🗄️ MySQL Configuration
Active profile: **mysql**

```
spring.datasource.url=jdbc:mysql://localhost:3306/tennisapp
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### Automatic CSV → MySQL Import
DataSeeder transfers CSV data into DB on first startup.

---

## 🎯 Backend Components
- PlayerController / MatchController / StatsController
- DTO validation
- ApiExceptionHandler
- PlayerJpaService + MatchJpaService
- Duplicate player name protection

---

## 🎯 Frontend Components
- LoginForm (BasicAuth without reload)
- AddPlayerForm (validation + clean error messages)
- PlayersTable (delete players)
- AddMatchForm
- MatchesTable (delete matches)
- LeaderboardTable
- api.js (REST service with error handler)

---

## 🔁 Key recent commits

### ✔ Duplicate-name validation  
Prevents overwriting existing players, returns clean JSON error.

### ✔ Full MySQL & JPA integration  
Repositories, entities, services, and automatic CSV import.

### ✔ Frontend authentication improvements  
Stable login state, removed unwanted logout.

### ✔ UI improvements  
Modern layout, forms, tables, styling.

---

## 🧪 Tests (Phase 8)
Includes 8 JUnit test classes:
- PlayerJpaServiceTest  
- MatchJpaServiceTest  
- StatsJpaServiceTest  
- PlayerControllerTest  
- MatchControllerCsvTest  
- StatsControllerTest  
- TestSecurityConfig
- TestWithoutSecurity

All tests pass successfully.

---

## 🌐 Deployment as static demo
Vite build is automatically copied into:
```
target/classes/static
```
http://pcolinsky.sk/

Upload to hosting:
- `index.html`
- `assets/`
- `vite.svg`

This creates a **static demo** without backend — perfect for portfolio.

---

## 🧑‍💻 Author
**Peter Pčolinský – TennisMate**  
Fullstack Java/React tennis management system.

