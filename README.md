# 🎾 TennisApp – REST API pre správu tenisových zápasov

## 🧩 Prehľad projektu
TennisApp je viacvrstvová Spring Boot aplikácia, ktorá umožňuje správu tenisových hráčov, zápasov a štatistík.
Projekt podporuje **dvojitý dátový režim**:
- **CSV režim (predvolený)** – dáta sa ukladajú do súborov.
- **JPA režim (profil `h2`)** – dáta sa ukladajú do H2 databázy cez Spring Data JPA a Hibernate.

---

## ⚙️ Použité technológie
- **Java 23 (OpenJDK)**
- **Spring Boot 3.3**
- **Spring Web / REST API**
- **Spring Data JPA + Hibernate**
- **H2 Database (profil `h2`)**
- **Lombok**
- **JUnit 5**
- **Postman (testovanie endpointov)**
- **Maven**

---

## 🧱 Štruktúra projektu

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

## 🧭 Fázy vývoja

| Fáza | Popis |
|------|--------|
| **1** | Konzolová aplikácia (CSV) |
| **2** | REST API – CSV režim |
| **3** | DTO a Controllers |
| **4** | Štatistiky a Leaderboard (CSV) |
| **5** | **JPA integrácia (H2, Hibernate)** – PlayerEntity, MatchEntity, StatsJpaService |
| **6** | **CRUD pre hráčov a zápasy (JPA)** – PlayerJpaService, MatchJpaService, dual mode (CSV ↔ H2) |

---

## 🗄️ Režimy aplikácie

### 🔹 CSV režim (predvolený)
Dáta sa načítavajú zo súborov `players.csv` a `matches.csv`.

### 🔹 JPA / H2 režim
Dáta sa načítavajú a ukladajú do databázy H2 (`./data/tenisdb.mv.db`).

#### Aktivácia:
V IntelliJ IDEA → **Run Configuration → Modify options → VM options:**
```
-Dspring.profiles.active=h2
```

V logu sa zobrazí:
```
The following 1 profile is active: "h2"
```

---

## 🚀 Spustenie projektu

1. Importuj projekt ako **Maven Project**  
2. Spusť `TenisApiApplication.java`  
3. Over v logu, že beží H2 alebo CSV profil  
4. Otestuj cez **Postman**:

| Operácia | Endpoint | Popis |
|-----------|-----------|--------|
| GET | `/api/players` | Načítanie hráčov |
| POST | `/api/players` | Pridanie hráča |
| DELETE | `/api/players/{name}` | Vymazanie hráča |
| GET | `/api/matches` | Načítanie zápasov |
| POST | `/api/matches` | Pridanie zápasu |
| DELETE | `/api/matches/{playerA}/{playerB}/{date}/{score}` | Vymazanie zápasu |
| GET | `/api/stats/leaderboard` | Štatistiky hráčov |
| GET | `/api/stats/player?name=Peter` | Štatistiky konkrétneho hráča |
| GET | `/api/stats/export` | Export leaderboardu do CSV |

---

## 🧪 Testovanie
Všetky endpointy boli otestované v Postmane:
- CRUD operácie pre hráčov a zápasy
- Štatistiky a export
- Filtrovanie podľa dátumu a hráča  
Všetky požiadavky odpovedajú `200 OK`.

---

## 🧩 Roadmap
- 🔜 Fáza 7 – migrácia na **MySQL**
- 🔜 Fáza 8 – jednotkové testy a refaktor
- 🔜 Fáza 9 – frontend (React) – Leaderboard, štatistiky, CRUD
- 🔜 Fáza 10 – Docker a CI/CD

---

## 👤 Autor
**Peter Pčolinský**  
GitHub: [PeterPcolinsky](https://github.com/PeterPcolinsky)
