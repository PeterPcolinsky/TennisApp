# 🎾 TennisMate – Fullstack tenisová aplikácia (Spring Boot + MySQL + React)

TennisMate je kompletná **fullstack aplikácia**, ktorá kombinuje:
- **Spring Boot (REST API)**
- **MySQL databázu**
- **JPA / Hibernate**
- **React (Vite) frontend**
- **Security (Basic Auth: ADMIN / USER)**
- **CSV fallback režim**
- **Validácie, štatistiky, leaderboard, zápasy, hráči**

Projekt slúži ako ukážka reálneho riešenia pre HR a developerov.

---

## 🚀 Hlavné funkcionality
- Login systém (ADMIN / USER)
- CRUD operácie pre hráčov
- Pridávanie/mazanie zápasov
- Výpočet štatistík (wins, losses, winrate)
- Live leaderboard podľa zápasov
- Validácie na backende aj fronte
- Ochrana pred duplicitou hráčov podľa mena
- MySQL perzistencia + automatický import CSV do DB
- Autentifikácia cez BasicAuth (bez browser popup okna)
- React frontend napojený na REST API

---

## 📦 Projektová štruktúra
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

## 🛑 Bezpečnosť & Autentifikácia
- ADMIN: `admin / admin911!`
- USER: `user / user`
- ADMIN môže:
  - pridávať hráčov
  - mazať hráčov
  - pridávať zápasy
  - mazať zápasy
- USER vidí len verejné údaje

Security vrstva rieši:
- odstránenie browser BasicAuth popupu
- custom 401 handler
- chránené `/api/**` endpointy

---

## 🗄️ MySQL – konfigurácia
Použitý profil: **mysql**

`application-mysql.properties`:
```
spring.datasource.url=jdbc:mysql://localhost:3306/tennisapp
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### Automatický import CSV → MySQL  
DataSeeder načíta CSV súbory a prepíše ich do MySQL pri prvom spustení.

---

## 🎯 Čo backend obsahuje
- PlayerController / MatchController / StatsController
- Validácie parameterov (DTO)
- Ošetrenie chýb (ApiExceptionHandler)
- PlayerJpaService & MatchJpaService
- Ochrana proti duplicitným hráčom

---

## 🎯 Čo frontend obsahuje
- AddPlayerForm (validácie + error handling)
- PlayersTable (mazanie hráčov)
- AddMatchForm (pridanie zápasu)
- MatchesTable (mazanie zápasov)
- LeaderboardTable (výpočty)
- LoginForm (BasicAuth bez reloadovania)
- api.js (REST volania + error handler)

---

## 🔁 Posledné dôležité zmeny (z commitov)

### ✔ Duplicate name validation  
- Ochrana pred prepisom existujúceho hráča  
- Čisté frontend chyby (❌ Hráč s týmto menom už existuje...)  

### ✔ Kompletné JPA + MySQL prepojenie  
- PlayerJpaService, MatchJpaService  
- repositories  
- DataSeeder automatický import  

### ✔ Frontend autentifikácia  
- odstránený page reload  
- stabilné sessionStorage  
- žiadne samovoľné odhlasovanie  

### ✔ UI úpravy  
- kompletný React frontend  
- layout, komponenty, tabuľky, formuláre  

---

## 🧪 Testy (Phase 8)
Súčasťou projektu je 8x JUnit testov:
- PlayerJpaServiceTest  
- MatchJpaServiceTest  
- StatsJpaServiceTest  
- PlayerControllerTest  
- MatchControllerCsvTest  
- StatsControllerTest  
- TestSecurityConfig
- TestWithoutSecurity

Všetky testy prešli úspešne.

---

## 🌐 Deployment ako statická ukážka
V `target/classes/static` sa automaticky vytvorí frontend (Vite build).  
http://pcolinsky.sk/

Na doménu je možné nahrať:
- `index.html`
- `assets/`
- `vite.svg`

Backend funkcie budú vypnuté (bez DB), ale UI bude viditeľné ako demo.

---

## 🧑‍💻 Autor
**Peter Pčolinský — TennisMate**  
Fullstack Java/React aplikácia pre registráciu hráčov a správu tenisových zápasov.

