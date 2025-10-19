# 🎾 TenisApp

**Konzolová aplikácia pre správu tenisového klubu** — eviduje hráčov, zápasy, štatistiky a výsledky.  
Cieľom projektu je ukázať princípy objektovo orientovaného programovania (OOP) v Jave na praktickom príklade.

---

## 🧩 Funkcionality
- Registrácia hráča (meno, vek, typ: Amatér / Profesionál)
- Výpočet výhernosť (win-rate)
- Vyhľadávanie hráča podľa mena (celé meno alebo časť)
- Odstránenie hráča
- Pridanie zápasu (hráči, výsledky setov, dátum)
- Zobrazenie všetkých zápasov
- Štatistiky hráča (zápasy, výhry, prehry, win-rate)
- Validácia vstupov (napr. meno obsahuje len písmená)
- Prehľadné menu pre používateľa

---

## 🧱 Štruktúra projektu
```
src/
 └── main/
     └── java/
         └── sk/
             └── peter/
                 └── tenis/
                     ├── model/       → Match, Player, PlayerType
                     ├── service/     → CsvService, StatsService
                     ├── ui/          → ConsoleApp
                     ├── util/        → Printer
                     └── App.java     → spúšťací bod aplikácie
```

---

## ⚙️ Použité technológie
- ☕ **Java 23**
- 🧩 **Maven**
- 💻 **IntelliJ IDEA**
- 🌐 **Git & GitHub**

---

## 🚀 Spustenie projektu

### 1️⃣ Klonovanie repozitára
```bash
git clone https://github.com/PeterPcolinsky/TennisApp.git
```

### 2️⃣ Spustenie v IntelliJ IDEA
1. Otvor projekt (súbor `pom.xml`)  
2. Spusti triedu `App.java`  
3. Konzolové menu sa automaticky zobrazí 🎾

---

## 🧠 Cieľ projektu
Projekt je súčasťou osobného Java študijného plánu *(August – December 2025)*.  
Postupne sa rozširuje o nové koncepty:
- Výnimky (Exceptions)  
- Kolekcie a Stream API  
- Unit testy (JUnit)  
- Databázová vrstva (MySQL)  
- REST API  
- Jednoduché grafické rozhranie (React)

---

## 👨‍💻 Autor
**Peter Pčolinský**  
📍 Slovensko  
🎯 Cieľ: stať sa **Junior Java Developerom v roku 2026**  
🔗 [GitHub – PeterPcolinsky](https://github.com/PeterPcolinsky)
