<div align="center">

# NexusBoosters

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Paper 1.21.x](https://img.shields.io/badge/Paper-1.21.x-blue.svg)](https://papermc.io/)
[![Gradle](https://img.shields.io/badge/Gradle-Build-green.svg)](https://gradle.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**An All-in-One Booster Management Plugin for PaperMC.**

</div>

## Overview
NexusBoosters is a comprehensive, production-ready booster management plugin for Paper. It provides seamless control over both personal and global boosters, complete with graphical user interfaces, asynchronous database operations, and deep integrations with popular third-party plugins.

## Features
*   **Personal & Global Scopes:** Support for individual boosters or server-wide events.
*   **Booster Types:**
    *   Vanilla XP Booster
    *   Vanilla Block Drop Booster
    *   Vanilla Mob Drop Booster
    *   Money/Economy Booster (Vault)
    *   PlayerPoints Booster (PlayerPoints)
    *   ShopGUI+ Buy/Sell/Category Boosters
*   **Graphical Interfaces:** Intuitive, paginated GUIs for players to view and activate tokens.
*   **Admin Tools:** Manage active boosters and distribute tokens easily.
*   **Cross-Server Synchronization:** Sync tokens and global boosters across multiple servers using MySQL.
*   **BossBar:** Display active global boosters prominently on screen.
*   **Bedrock Fallback:** Text-based menus for players using Floodgate/Geyser.
*   **PlaceholderAPI Integration:** Expose your multipliers to scoreboards and chat formatting.

## Requirements & Dependencies
NexusBoosters strictly requires **Java 21** and **Paper 1.21.x**. 

| Dependency | Required | Purpose |
| :--- | :---: | :--- |
| Paper | **Yes** | Server platform API |
| Vault | Optional | Required for Economy (`MONEY`) boosters |
| PlaceholderAPI | Optional | Required for custom placeholders |
| Floodgate | Optional | Required for Bedrock GUI fallback |
| PlayerPoints | Optional | Required for `PLAYERPOINTS_GAIN` boosters |
| ShopGUI+ | Optional | Required for Shop discount/sell boosters |

## Installation
1. Download or build the `NexusBoosters-1.0.0.jar`.
2. Stop your server.
3. Place the JAR into your server's `plugins` folder.
4. Install any optional dependencies you plan to use.
5. Start your server to generate the configuration files.
6. Edit the config in `plugins/NexusBoosters/`.
7. You can safely use `/nb reload` for config changes. A full restart is required if changing database engines.

## Quick Start
1. Define a booster in `boosters.yml`:
```yaml
weekend_xp:
  type: XP
  scope: GLOBAL
  multiplier: 2.0
  duration: 3600
  material: EXPERIENCE_BOTTLE
  name: "&aDouble XP Booster"
```
2. Give a player a token: `/nb give Steve weekend_xp 1`
3. Have the player type `/nb open` to open their GUI and click the token to activate it.
4. View active multipliers with `/nb active`.

## Booster Types and Scopes

| Type | Effect | Required Hook |
| :--- | :--- | :--- |
| `MONEY` | Multiplies economy transactions | Vault |
| `XP` | Multiplies vanilla experience drops | None |
| `BLOCK_DROPS` | Multiplies drops from broken blocks | None |
| `MOB_DROPS` | Multiplies drops from killed mobs | None |
| `PLAYERPOINTS_GAIN` | Multiplies PlayerPoints rewards | PlayerPoints |
| `SHOP_BUY_DISCOUNT` | Reduces buy prices | ShopGUI+ |
| `SHOP_SELL` | Increases sell prices | ShopGUI+ |
| `SHOP_CATEGORY_SELL` | Increases sell prices in specific categories | ShopGUI+ |
| `CUSTOM` | No internal effect; relies on external plugin API | None |

| Scope | Description |
| :--- | :--- |
| `PERSONAL` | The multiplier only applies to the player who activated the booster. |
| `GLOBAL` | The multiplier applies to all players currently on the server. |
| `SERVER` | Functions identically to global, applying to the server environment. |

## Documentation

Full documentation is available in the `docs/` directory:
- [Installation Guide](docs/installation.md)
- [Configuration Guide](docs/configuration.md)
- [Boosters Guide](docs/boosters.md)
- [Commands and Permissions](docs/commands-permissions.md)
- [Database Setup](docs/database.md)
- [Cross-Server Sync](docs/cross-server.md)
- [Integrations](docs/integrations.md)
- [GUI Configuration](docs/gui.md)
- [PlaceholderAPI List](docs/placeholders.md)
- [Troubleshooting](docs/troubleshooting.md)
- [Development Guide](docs/development.md)

## Known Limitations
*   Due to caching and polling mechanisms, cross-server MySQL synchronization may take up to the configured interval (e.g., 5 seconds) to reflect changes on secondary servers.
*   Personal boosters are bound to the server they are activated on and do not synchronize cross-server.

## Support and Bug Reports
If you encounter a bug, please open an Issue on our GitHub repository.
Ensure you include:
1. Your NexusBoosters version.
2. Your Paper and Java versions.
3. A list of relevant plugins (Vault, ShopGUI+, etc.).
4. The relevant sections of your configurations.
5. Exact steps to reproduce the issue.
6. The full stack trace from your console if an error occurred.

## Credits
NexusBoosters is a modern, optimized rework inspired by the conceptual design of AxBoosters. NexusBoosters is an independent project and is not affiliated with, endorsed by, or developed by the creators of AxBoosters. All code is original and proprietary assets from the original plugin have not been used.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
