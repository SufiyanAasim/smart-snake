# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [4.0.0] - 2026-07-16
### Codename: dbserpent

### Added
- relational SQLite database score tracker via local JDBC.
- persistent database manager `DatabaseManager.java`.
- interactive leaderboard table dialog `LeaderboardDialog.java` showing historic scores.
- query-based row deletion, wipe database actions, and CSV scores export.
- sidebar dashboard button to launch the Scoreboard/Leaderboard overlay.

---

## [3.0.0] - 2026-07-16
### Codename: synthserpent

### Added
- strict Model-View-Controller (MVC) architecture refactoring.
- sidebar control HUD panel expanding window from 800px to 1100px.
- controller mode toggles (Manual, A* Pathfinding, Q-Learning).
- active statistical overlays (steps, path efficiency index, state vector).
- UI sliders to adjust tick rates (speed) and reinforcement learning epsilon ($\epsilon$) value.
- real-time A* planned path visualization overlay.

---

## [2.0.0] - 2026-07-16
### Codename: cyberserpent

### Added
- A* pathfinding autoplay solver with Manhattan heuristic.
- BFS safety search fallback pointing to tail or maximum empty nodes.
- Q-learning reinforcement learning agent with 128 states.
- head-offset barrier block obstacles spawning dynamically with score.
- connection analysis to prevent food from spawning inside obstacles or loops.
- progressive difficulty speed increases.

---

## [1.0.0] - 2026-07-16
### Codename: retroserpent

### Added
- baseline classic keyboard-controlled gameplay mechanics.
- directional input buffer to prevent self-collision.
- growing snake logic and randomized food spawning.
- green-to-dark-green trailing gradient graphics.
- session-based high score tracker.
- local Java 21 compilation and runnable JAR script (`build_and_run.ps1`).
