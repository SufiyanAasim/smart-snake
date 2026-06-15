# Smart Snake Game Roadmap

This roadmap tracks the development progress of the Smart Snake Game application:

```mermaid
gantt
    title Smart Snake Game Development
    dateFormat  YYYY-MM-DD
    section Phase 1 (Baseline)
    Classic Game Mechanics (v1.0.0) :done, des1, 2026-07-10, 2026-07-16
    section Phase 2 (AI Agents)
    A* Pathfinder Solver (v2.0.0)    :active, des2, 2026-07-16, 2d
    Q-Learning Agent (v2.0.0)       :active, des3, 2026-07-16, 2d
    Dynamic Obstacles (v2.0.0)      :active, des4, 2026-07-16, 2d
    section Phase 3 (GUI Modernization)
    MVC Refactoring (v3.0.0)        :crit, des5, 2026-07-17, 3d
    Neon Dashboard Sidebar (v3.0.0) :crit, des6, 2026-07-17, 3d
    section Phase 4 (relational Persistence)
    SQLite Relational Scores (v4.0.0) :des7, 2026-07-20, 2d
    Interactive Scoreboard (v4.0.0)  :des8, 2026-07-20, 2d
```

## Release Summary
* **v1.0.0 (`retroserpent`)**: Establish baseline play, controls, and local packaging.
* **v2.0.0 (`cyberserpent`)**: Integrate perfect-play A* graph search and compact reinforcement learning agent.
* **v3.0.0 (`synthserpent`)**: Modernize visual rendering layout and implement real-time dashboards.
* **v4.0.0 (`vaultserpent`)**: Persistent relational scoreboard logging and interactive statistics panels.
