# 🎾 TenisApp (REST API verzia)

**Spring Boot REST API + CSV backend**  
Aplikácia pre evidenciu hráčov tenisového klubu.  
Projekt pôvodne vznikol ako konzolová verzia a bol rozšírený o REST API vrstvu.

---

## 🧩 Funkcionality
### 👥 Hráči (Players)
- `GET /api/players` – zobrazí všetkých hráčov  
- `POST /api/players` – pridá nového hráča  
- Validácia vstupov pomocou `@Valid`  
- Chybové správy v JSON formáte (`ApiExceptionHandler`)  
- Ukladanie dát do súboru `data/players.csv`

### 🎾 Zápasy (Matches)
*(bude doplnené v ďalšej fáze)*  
- `GET /api/matches` – načítanie zápasov  
- `POST /api/matches` – pridanie zápasu s overením hráčov

---

## 🧱 Štruktúra projektu
```
src/
 └── main/
     ├── java/sk/peter/tenis/
     │    ├── controller/      → REST controllery
     │    ├── dto/             → Data Transfer Objects (PlayerDto)
     │    ├── exception/       → ApiExceptionHandler
     │    ├── model/           → Player, PlayerType, Match
     │    ├── service/         → CsvService, PlayerService
     │    └── TenisApiApplication.java → spúšťací bod aplikácie
     └── resources/
          ├── application.properties → konfigurácia cesty k CSV
          └── data/players.csv       → uložené dáta hráčov
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
```json
POST http://localhost:8080/api/players
{
  "name": "Novak Djokovic",
  "age": 37,
  "type": "PROFESIONAL"
}
```

---

## 🧠 Cieľ projektu
Projekt je súčasťou osobného Java študijného plánu *(August – December 2025)*.  
Cieľ: vytvoriť plnohodnotnú backend aplikáciu s REST API, validáciami a CSV perzistenciou.  
Ďalšie plánované rozšírenia:
- Úprava a mazanie hráčov (PUT, DELETE)  
- Evidencia zápasov (GET/POST `/api/matches`)  
- Prepojenie s databázou (MySQL, Hibernate)  
- Frontend rozhranie (React)

---

## 👨‍💻 Autor
**Peter Pčolinský**  
📍 Slovensko  
🎯 Cieľ: stať sa **Junior Java Developerom v roku 2026**  
🔗 [GitHub – PeterPcolinsky](https://github.com/PeterPcolinsky)

---

**🇬🇧 [English version →](README_EN.md)**
>