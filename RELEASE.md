# Release Cycle and Documentation

This document describes the release cycle, packaging requirements, and codename guidelines for the Smart Snake project.

## Semantic Versioning
We adhere strictly to Semantic Versioning (MAJOR.MINOR.PATCH):
- **MAJOR**: Breaking changes or major architecture shifts (e.g., refactoring to MVC in `v3.0.0`, SQLite integration in `v4.0.0`).
- **MINOR**: New capabilities without breaking backwards compatibility (e.g., adding A* autoplay in `v2.0.0`).
- **PATCH**: Bug fixes, performance tweaks, and documentation edits.

## Release Codenames
Codenames use a theme centered around **"Serpent"** without spaces:
- **v1.0.0**: `Retroserpent`
- **v2.0.0**: `Cyberserpent`
- **v3.0.0**: `Synthserpent`
- **v4.0.0**: `Vaultserpent`
- **v5.0.0**: `Glowserpent`
- **v6.0.0**: `Portalserpent`
- **v7.0.0**: `Battleserpent`

## Release Branches
- Code is developed in `develop` and merged into `release/vX.Y.Z` for staging.
- Production stable releases are merged into `master` and tagged with `vX.Y.Z`.
