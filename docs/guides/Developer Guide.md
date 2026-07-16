# 🛠️ Smart Snake Game - Developer & Compilation Guide

This guide is designed for developers who wish to modify, compile, and package the Smart Snake Game Java source files and the C# native launcher.

---

## 1. Project Directory Layout
```text
Smart Snake Game/
├── assets/                 # Image and icon assets (logo.png, logo.ico)
├── docs/                   # System guides and release notes
├── dist/                   # Output folder for compiled runnable JAR
├── lib/                    # Package libraries (SQLite driver, SLF4J loggers)
├── src/project/            # Java Swing MVC source files
├── src_launcher/           # C# Launcher source files (.csproj and Program.cs)
├── build_and_run.ps1       # Compiles and packages Java files instantly
└── SmartSnakeGame.exe      # Native Windows Launcher binary
```

---

## 2. Compile and Package Java Engine
### Requirements
* **Java Development Kit (JDK) 21** must be installed on the system.

### Using the PowerShell Build Wrapper
To automate compiling of source files and packaging of the JAR:
1. Open a PowerShell terminal.
2. Run:
   ```powershell
   ./build_and_run.ps1
   ```
This script compiles Java classes using the linked libraries in `/lib`, packages them into `dist/SmartSnakeGame.jar`, and runs the game.

### Manual Compilation Commands
If you prefer running commands manually:
1. **Compile Java Files**:
   ```bash
   javac -cp "lib/sqlite-jdbc.jar;lib/slf4j-api.jar;lib/slf4j-simple.jar" -d out src/project/*.java
   ```
2. **Package JAR file**:
   ```bash
   jar --create --file dist/SmartSnakeGame.jar --main-class project.Project -C out project
   ```

---

## 3. Compile C# Native Windows Launcher
### Requirements
* **.NET 10 SDK** (installed on the developer machine).

### Compilation Commands
The C# launcher program is stored under `src_launcher/`. To compile it into a single-file executable containing the embedded game icon:
1. Navigate to the root workspace.
2. Publish the project using the .NET command line utility:
   ```bash
   dotnet publish src_launcher -c Release -o dist -r win-x64 --self-contained false -p:PublishSingleFile=true -p:UseAppHost=true
   ```
3. Copy the compiled executable to the root directory and rename it:
   ```powershell
   Move-Item -Path "dist/Launcher.exe" -Destination "SmartSnakeGame.exe" -Force
   ```
This updates the **`SmartSnakeGame.exe`** binary. You do not need to rebuild the launcher unless you change its launching parameters or change the icon resource.

---

## 4. Coding Conventions & Standards
* **MVC Pattern**: Keep model data (scores, boundaries, directions) distinct from UI elements (buttons, grids).
* **Toroidal Wrapping Math**: When designing solvers or movement tick processes, check `model.getBorderMode()` state:
  * `"Solid"`: Standard boundary collision.
  * `"Wrap"`: Coordinate translation ($x \ge \text{width} \rightarrow x = 0$).
* **API Backward Compatibility**: Keep overloaded methods in helper agents (e.g. `Pathfinder.findPath()`, `QLearningAgent.getState()`) without wrapping parameters to avoid breaking legacy monolith source files like `Snakegame.java` during compilation.
* **Credits Attribution**: Ensure credits align with authorship:
  * **Mohammad Sufiyan Aasim**: **Lead AI & Machine Learning Engineer**
  * **Fahad Bin Nasir**: **Lead MVC & Systems Architect**
