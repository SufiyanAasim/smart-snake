# Release Notes - v1.0.0-beta
# Release Name: "RetroSerpent: Genesis"

**Release Date**: July 16, 2026  
**Target Platform**: Java 21 (OpenJDK)  
**Binary Output**: [SmartSnakeGame.jar](file:///d:/Completed%20Github%20Projects%20%28Fully%20Tested%20&%20Deployed%29/Smart%20Snake%20Game/dist/SmartSnakeGame.jar)

---

## Executive Summary

**"Retro Serpent: Genesis" (v1.0.0-beta)** is the foundational pre-release of the Snake Game project. It contains the complete classic gameplay mechanics, rendering layers, scoring systems, and build tools. Written entirely in **Java 21** using standard Swing components, this release establishes a solid, modern baseline before introducing intelligent AI capabilities, reinforcement learning, and advanced game modes.

---

## Key Features

### 1. Retro Gameplay & Physics
- **Cell-Based Grid System**: A structured play area of **40 x 30 cells** based on an 800x600 screen and 20px cell sizes.
- **Directional Buffering**: Prevents the snake from collapsing into its own neck when two keystrokes are registered in rapid succession within a single game tick.
- **Classic Grow Mechanic**: Consuming red food items increases the snake's length and score while triggering random food respawns that avoid spawning inside the snake's body.

### 2. High-Performance Rendering & Aesthetics
- **Trailing Tail Gradient**: Visual rendering logic that applies a dynamic multiplication color filter (`Color * 0.85`) to snake segments, producing a fading trailing green gradient that visually represents speed and movement direction.
- **Text Layout Centering**: Centered overlay text rendering for menu instructions and game over states.

### 3. Session High-Score Tracker
- Monitors player performance throughout the session, storing and updating the high score in real-time, displaying it on the post-game reset screen.

### 4. Runnable Build Tools
- Includes `build_and_run.ps1`, a PowerShell build automation script that automatically detects local JDK 21 paths (such as Android OpenJDK), compiles code, packages class files into a single runnable JAR, and launches the application.

---

## Code Base & Language Features

This pre-release utilizes advanced features of **Java 21**:
* **Records**: Implements `record GamePoint(int x, int y)` for lightweight, immutable data structures.
* **Switch Expressions**: Streamlines calculations of coordinates depending on the enum:
  ```java
  final GamePoint newHead = switch (direction) {
      case UP -> new GamePoint(head.x, head.y - cellSize);
      case DOWN -> new GamePoint(head.x, head.y + cellSize);
      case LEFT -> new GamePoint(head.x - cellSize, head.y);
      case RIGHT -> new GamePoint(head.x + cellSize, head.y);
  };
  ```
* **Type Inference**: Uses `final var` to reduce boilerplate.

---

## Known Discrepancies (V1 Feedback)
- **Menu Instructions**: The starting overlay says "Kill Enemy", which is a legacy/placeholder phrase as there are currently no enemy elements in the game.
- **Manual Input Only**: The snake requires manual keyboard inputs; it has no autopilot or pathfinding logic.

---

## Packaging & How to Run
To compile and execute this release:
1. Open a PowerShell terminal in the project directory.
2. Run the build script:
   ```powershell
   ./build_and_run.ps1
   ```
3. A standalone executable binary will be generated at `dist/SmartSnakeGame.jar`.
