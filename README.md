# X-and-O (Tic-Tac-Toe)

![image](https://github.com/user-attachments/assets/4c9ecadf-3cd6-411f-a3c7-09d1e440c089)

# Goooood old Tic-Tac-Toe game made in Java! 
# ദ്ദി(˵ •̀ ᴗ - ˵ ) ✧
# You can play against the computer 🤖or another person. 

# The game has a Kool retro look with bright colors and easy menus.
#  	ENJOY ! :P


music and sound effects!!!

# Features

## Play with a friend or against the computer
![image](https://github.com/user-attachments/assets/c27a0a5a-2df7-4fbf-b749-1c7d085b7563)


## Choose the board size (3x3 up to 6x6)
![image](https://github.com/user-attachments/assets/1e89443e-0367-42fc-8ae8-a02a6161c946)

  
## See who’s winning and how much time has passed
  ![image](https://github.com/user-attachments/assets/5fa3e9c2-3772-4a00-ba08-d37c1f3e3dca)

## Restart the game or go back to the menu any time
![image](https://github.com/user-attachments/assets/d22da913-8151-45a0-9476-8cdb2fd9e20b)

---

# How to Play! ![image](https://github.com/user-attachments/assets/a15763c7-6eb1-440b-a854-bc08fed12555)


# JUST CLICK THE  Tic-tac-toe.JAR or Tic-tac-toe.EXE ![image](https://github.com/user-attachments/assets/a15763c7-6eb1-440b-a854-bc08fed12555)

1.  **JUST CLICK THE  Tic-tac-toe.JAR !** OR 1.

     **JUST CLICK THE  Tic-tac-toe.EXE !**

   
3.  **Download or clone this project:**
    ```sh
    git clone [https://github.com/ALNezar/X-and-O.git](https://github.com/ALNezar/X-and-O.git)
    cd X-and-O
    ```

4.  **Run the game:**
    * **If you use an IDE (like IntelliJ IDEA or Eclipse):**
        Open the project and run `Main.java` directly. The IDE will automatically handle the necessary dependencies.
    * **If you use the command line:**
        You need to include the SQLite JDBC driver (`sqlite-jdbc-3.27.2.1.jar`) in your classpath.

        **For Windows:**
        ```sh
        # Compile all Java files, including the JDBC driver in the classpath
        javac -cp ".;sqlite-jdbc-3.27.2.1.jar" *.java

        # Run the main application
        java -cp ".;sqlite-jdbc-3.27.2.1.jar" Main
        ```
        **For macOS/Linux:**
        ```sh
        # Compile all Java files, including the JDBC driver in the classpath
        javac -cp ".:sqlite-jdbc-3.27.2.1.jar" *.java

        # Run the main application
        java -cp ".:sqlite-jdbc-3.27.2.1.jar" Main
        ```

## Files in This Project

* `Main.java` — run de game!docs(readme): Update installation and project structure details
* `TicTacToeApp.java` — Manages the overall app and screens.
* `GamePanel.java` — The main game screen.
* `WelcomePanel.java` — The welcome and main menu.
* `SettingsPanel.java` — Handles game settings.
* `Settings.java` — Stores game settings in memory.
* `resources/` — Contains game assets like audio files (`.wav`).
* `sqlite-jdbc-3.27.2.1.jar` — The JDBC driver for SQLite database operations.
* `settings.db` — The SQLite database file (for persistent settings).

---

## Credits

🎵 **Soundtrack by [TigerOffbtk](https://www.youtube.com/channel/UC58Ar0632X6zpvv7on1Kjkg)**  
Big thanks for creating the awesome soundtrack :)
---

## License

This project is open source and free to use.

---
Made by [ALNezar](https://github.com/ALNezar)
