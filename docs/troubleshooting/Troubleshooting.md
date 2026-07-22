# 🔍 Smart Snake - Troubleshooting Guide

This guide lists common problems developers or users might encounter when compiling or launching the Smart Snake, along with their solutions.

---

## 1. SmartSnake.exe fails to launch
* **Symptom**: Double-clicking `SmartSnake.exe` does nothing or exits instantly.
* **Resolution**:
  1. Ensure **Java 21 JRE/JDK** is installed on your computer. You can check this by opening a command prompt and typing:
     ```bash
     java -version
     ```
  2. If the command fails, Java is either not installed or is not added to your system environment variables. Download Java 21 from [Adoptium](https://adoptium.net/) and add the `bin` directory of your Java installation to the system's `PATH` variable.
  3. Check if `launcher_error.txt` was generated in the root directory of the project. If it exists, read it for details regarding the crash.

---

## 2. NoClassDefFoundError: org/slf4j/LoggerFactory
* **Symptom**: The Java application crashes at boot with a stack trace mentioning `org/slf4j/LoggerFactory`.
* **Resolution**:
  * SQLite JDBC requires the SLF4J logging API to run. Ensure that `lib/slf4j-api.jar` and `lib/slf4j-simple.jar` are present in your local `lib/` directory and are linked in the classpath compile parameter (`-cp`).
  * If using `build_and_run.ps1`, verify that the script has the updated classpath parameter:
    `-cp "dist/SmartSnake.jar;lib/sqlite-jdbc.jar;lib/slf4j-api.jar;lib/slf4j-simple.jar"`

---

## 3. SQLite Database Connection Failures
* **Symptom**: High scores are not logged, or the Scoreboard table displays empty or throws SQL errors.
* **Resolution**:
  * Verify that the database file path configured in your `.env` settings exists.
  * Ensure the application has read/write permissions for the `data/` subdirectory.
  * Check that `lib/sqlite-jdbc.jar` is correctly packaged inside the `lib/` folder.

---

## 4. C# Launcher Single File Compilation Error (CS5001)
* **Symptom**: Compiling the C# launcher project via `dotnet publish` throws: `CSC : error CS5001: Program does not contain a static 'Main' method suitable for an entry point`.
* **Resolution**:
  * Double check that `src_launcher/Program.cs` contains the entry point:
    `static void Main(string[] args)`
  * Ensure the namespace matches and that you run the `dotnet publish` command pointing to the project folder (`src_launcher`) rather than compiling individual files.
