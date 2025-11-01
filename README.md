# 🎾 Tenisová aplikácia – REST API (Spring Boot)

Projekt vytvorený v Jave (Spring Boot), ktorý spracováva údaje o tenistoch a zápasoch cez REST API.
Podporuje **CSV** aj **MySQL (JPA)** režim a umožňuje CRUD operácie pre hráčov a zápasy.

---

## 🚀 Aktuálny stav projektu

✅ REST API plne funkčné  
✅ CRUD operácie pre hráčov aj zápasy  
✅ Štatistiky (výhry, prehry, percentá úspešnosti)  
✅ Postman testy úspešne prebehnuté  
✅ MySQL migrácia hotová (JPA integrácia)  
🔜 Fáza 8 – JUnit testy a refaktor

---

## 🧩 Fázy vývoja

| Fáza | Názov | Stav |
|------|--------|------|
| 1 | CSV načítanie a ukladanie | ✅ Hotovo |
| 2 | REST API pre hráčov a zápasy | ✅ Hotovo |
| 3 | Štatistiky hráčov | ✅ Hotovo |
| 4 | DTO a validácie | ✅ Hotovo |
| 5 | Exception handling | ✅ Hotovo |
| 6 | JPA integrácia (H2 databáza) | ✅ Hotovo |
| 7 | MySQL migrácia (JPA integrácia + DataSeeder) | ✅ Hotovo |
| 8 | JUnit testy a refaktor | 🔜 Nasleduje |
| 9 | React frontend (Leaderboard UI) | 🔜 Budúce rozšírenie |

---

## 🗂️ Štruktúra projektu

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
 │   │   ├─ DataSeeder.java         ← NEW (import CSV → MySQL pri štarte)
 │   │   └─ TenisApiApplication.java
 │   └─ resources/
 │       ├─ application.properties
 │       ├─ application-h2.properties
 │       └─ application-mysql.properties   ← NEW (MySQL konfigurácia)
 ├─ test/
 │   ├─ PlayerControllerTest.java
 │   ├─ MatchControllerTest.java
 │   └─ StatsControllerTest.java
 └─ data/
     ├─ players.csv
     └─ matches.csv
```

---

## ⚙️ MySQL profil

Aplikácia sa spúšťa s profilom `mysql`, ktorý využíva skutočnú databázu namiesto H2.

### Nastavenie (application-mysql.properties)
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

## 🧠 Testovanie cez Postman

Testované CRUD operácie:

| Operácia | Endpoint | Výsledok |
|-----------|-----------|----------|
| GET | `/api/players` | Zobrazí všetkých hráčov |
| POST | `/api/players` | Pridá nového hráča |
| PUT | `/api/players/{name}` | Aktualizuje hráča |
| DELETE | `/api/players/{name}` | Odstráni hráča |
| GET | `/api/matches` | Zobrazí všetky zápasy |
| POST | `/api/matches` | Pridá nový zápas |
| PUT | `/api/matches/{id}` | Aktualizuje zápas podľa ID |
| DELETE | `/api/matches/{id}` | Vymaže zápas podľa ID |

---

## 🧾 Databázová integrácia

✅ Automatický import CSV → MySQL cez `DataSeeder` pri štarte aplikácie  
✅ Reálne tabuľky `players` a `matches` v MySQL (phpMyAdmin)  
✅ Transakcie cez Hibernate (JPA repository)  
✅ Okamžitý sync s databázou (bez potreby GET obnovy)

---

## 📅 Ďalší krok

➡️ **Fáza 8 – JUnit testy a refaktor (PlayerControllerTest, MatchControllerTest, StatsControllerTest)**  
➡️ následne **Fáza 9 – React frontend (Leaderboard UI)**

---

**Autor:** Peter Pčolinský  
**GitHub:** [PeterPcolinsky](https://github.com/PeterPcolinsky)
