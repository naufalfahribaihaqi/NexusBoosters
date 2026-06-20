# Developer Guide

Information for contributing and API usage.

## Overview

NexusBoosters uses Paper 1.21.x API and compiles with Gradle.

## Build Command

```bash
./gradlew clean build
```

Compiled files output to `build/libs/`.

## Architecture Overview

```text
Command / GUI
      |
BoosterManager
      |
StorageService
      |
SQLite / MySQL
```

* **BoosterManager**: Handles active multipliers and caching logic.
* **StorageService**: Abstraction for SQL queries.
* **Listeners**: Hooks into Bukkit events.

## Threading Rules

NexusBoosters strictly separates database I/O.

* **Never** access the Bukkit API (e.g., `Player#sendMessage`) from an asynchronous thread.
* **Always** use `Bukkit.getScheduler().runTask()` to sync back to the main thread after an async query completes.

---

[← Previous](Troubleshooting) | [Home](Home)
