# 🎾 TennisApp

**Console application for managing a tennis club** — keeps track of players, matches, statistics, and results.  
The goal of this project is to demonstrate the principles of Object-Oriented Programming (OOP) in Java through a practical example.

---

## 🧩 Features
- Register player (name, age, type: Amateur / Professional)
- Calculate win-rate
- Search player by name (full or partial)
- Remove player
- Add match (players, score by sets, date)
- List all matches
- Player statistics (matches, wins, losses, win-rate)
- Input validation (e.g., name contains only letters)
- Clear console menu navigation

---

## 🧱 Project structure
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
                     └── App.java     → entry point of the application
```

---

## ⚙️ Technologies used
- ☕ **Java 23**
- 🧩 **Maven**
- 💻 **IntelliJ IDEA**
- 🌐 **Git & GitHub**

---

## 🚀 How to run

### 1️⃣ Clone the repository
```bash
git clone https://github.com/PeterPcolinsky/TennisApp.git
```

### 2️⃣ Run in IntelliJ IDEA
1. Open the project (select `pom.xml`)  
2. Run the `App.java` class  
3. Console menu will appear automatically 🎾

---

## 🧠 Project goal
This project is part of a personal **Java learning plan (August – December 2025)**.  
It will gradually expand to include new concepts:
- Exceptions handling  
- Collections and Stream API  
- Unit testing (JUnit)  
- Database layer (MySQL)  
- REST API  
- Simple frontend interface (React)

---

## 👨‍💻 Author
**Peter Pčolinský**  
📍 Slovakia  
🎯 Goal: become a **Junior Java Developer in 2026**  
🔗 [GitHub – PeterPcolinsky](https://github.com/PeterPcolinsky)
