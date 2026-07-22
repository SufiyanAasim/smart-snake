# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [7.0.0] - 2026-07-22
### Codename: Battleserpent

### Added
- Programmatic 8-bit chiptune MIDI sound synthesizer engine (`SoundManager.java`).
- Real-time Visual Theme Customizer (Cyberpunk Neon, Vaporwave Pink, Matrix Green).
- Target variations: Golden Apples (+3 score boost) and Blue Shield Orbs (head bubble outline shield).
- Obstacle destruction mechanic when crashing with an active head shield.
- Map Creator & Obstacle Painter (canvas click/drag custom walls painting).
- Rival AI competitor snake mode chasing targets and blocking player.
- Symmetrical 3x2 actions buttons layout with orange Help and Sound toggles.
- Dedicated full-width Exit button that terminates JVM and cleans up timer resources.
- Automatic pause gameplay trigger when launching modal dialogue overlays.

### Changed
- Pause button label toggles dynamically to Resume during active paused states.
- Increased Controller Mode HUD box spacing parameter to 120px to prevent text clipping.
- Re-spelled all visible occurrences of Battle Serpent to Battleserpent.

---

## [6.0.0] - 2026-07-16
### Codename: Portalserpent

### Added
- Toroidal coordinate wrapping (Portal Mode teleportation physics).
- Border Physics selection dropdown selector in speed options.
- Wrapped A* distance heuristics and neighbor calculations in `Pathfinder.java`.
- Wrapped reinforcement learning sensors in `QLearningAgent.java`.

### Changed
- Refactored build pipeline to compile classes directly to `out/` folder.
- Removed IntelliJ nested out folders and boilerplate config directories.

---

## [5.0.0] - 2026-07-02
### Codename: Glowserpent

### Added
- Aspect ratio scale preservation with Centered Letterbox rendering.
- Pause state overlays showing screen center notices.
- Global Swing Key Bindings migration to resolve sidebar widget focus-loss lockouts.
- Custom styled themed confirmation dialogues (`ConfirmDialog.java`).
- Symmetrical custom instructions help dialog panel (`HelpDialog.java`).
- Background thread offloader for initial reinforcement learning weights pre-training.

### Fixed
- Scoreboard sqlite ResultSet cursor leaks by utilizing nested try-with-resources.
- Score reset mapping value offset starting at `0` instead of `1` on fresh games.

---

## [4.0.0] - 2026-06-01
### Codename: Vaultserpent

### Added
- Relational SQLite database score tracker via local JDBC.
- Persistent database manager `DatabaseManager.java`.
- Interactive leaderboard table dialog `LeaderboardDialog.java` showing historic scores.
- Query-based row deletion, wipe database actions, and CSV scores export.
- Sidebar dashboard button to launch the Scoreboard/Leaderboard overlay.

---

## [3.0.0] - 2025-03-10
### Codename: Synthserpent

### Added
- Strict Model-View-Controller (MVC) architecture refactoring.
- Sidebar control HUD panel expanding window from 800px to 1100px.
- Controller mode toggles (Manual, A* Pathfinding, Q-Learning).
- Active statistical overlays (steps, path efficiency index, state vector).
- UI sliders to adjust tick rates (speed) and reinforcement learning epsilon ($\epsilon$) value.
- Real-time A* planned path visualization overlay.

---

## [2.0.0] - 2024-06-15
### Codename: Cyberserpent

### Added
- A* pathfinding autoplay solver with Manhattan heuristic.
- BFS safety search fallback pointing to tail or maximum empty nodes.
- Q-learning reinforcement learning agent with 128 states.
- Head-offset barrier block obstacles spawning dynamically with score.
- Connection analysis to prevent food from spawning inside obstacles or loops.
- Progressive difficulty speed increases.

---

## [1.0.0] - 2023-11-12
### Codename: Retroserpent

### Added
- Baseline classic keyboard-controlled gameplay mechanics.
- Directional input buffer to prevent self-collision.
- Growing snake logic and randomized food spawning.
- Green-to-dark-green trailing gradient graphics.
- Session-based high score tracker.
- Local Java 21 compilation and runnable JAR script (`build_and_run.ps1`).
