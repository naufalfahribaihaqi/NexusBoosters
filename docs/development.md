# Development Guide

This guide provides information for developers looking to contribute to NexusBoosters or compile it themselves.

## Build Environment
*   **JDK:** Java 21 is strictly required.
*   **Build Tool:** Gradle (Wrapper provided).
*   **API:** Paper API 1.21.x.

## Compiling from Source
Clone the repository and run the Gradle build task:
```bash
git clone https://github.com/naufalfahribaihaqi/NexusBoosters.git
cd NexusBoosters
./gradlew clean build
```
The compiled jar will be located in `build/libs/`. 

## Threading Rules
NexusBoosters relies heavily on asynchronous database calls (HikariCP for MySQL, standard JDBC for SQLite). 
**Rule of Thumb:** 
*   Never access the Bukkit API (e.g., `Player#sendMessage`, `Inventory#setItem`) from an asynchronous thread.
*   Always use `Bukkit.getScheduler().runTask()` to jump back to the main thread when updating the UI or sending messages after a database query completes.

## Adding a New Booster Type
1. Add the enum entry to `BoosterType.java`.
2. Determine if it requires an external integration. If so, add a hook in `id.naufal.nexusboosters.hook`.
3. If it's a vanilla feature, create a new listener in `id.naufal.nexusboosters.listener`.
4. Register your listener in `NexusBoostersPlugin.java`.
5. Fetch the active multiplier using `plugin.getBoosterManager().getActiveMultiplier(uuid, BoosterType)`.

## Pull Requests
Before submitting a PR, ensure you have read the `.github/pull_request_template.md` and checked off all requirements.
