# NexusBoosters v1.0.0 Release Notes

Welcome to the official 1.0.0 release of **NexusBoosters**!

## 📖 Plugin Overview
NexusBoosters is a production-ready, highly optimized, and feature-rich Minecraft booster plugin. Designed from the ground up for modern server architectures, it provides flexible multiplier systems for XP, Economy, and Drops without blocking your main thread or relying on bloated libraries.

## ⚙️ Requirements
- **Server Version:** Paper 1.21.11 (Spigot/Bukkit are not officially supported due to Paper-exclusive API usage)
- **Java Requirement:** Java 21+

## ✨ Features
- **Zero-Lag Architecture:** All SQLite operations and database I/O run completely asynchronously.
- **Multiple Booster Scopes:** Supports `GLOBAL` (server-wide) and `PERSONAL` (player-specific) boosters.
- **Dynamic Multipliers:** Features `XP`, `MONEY`, `BLOCK_DROPS`, and `MOB_DROPS` booster types natively.
- **Smart GUI System:** Fully customizable inventory GUIs with built-in pagination for Java players.
- **Bedrock Native Fallbacks:** Intelligently detects Geyser/Floodgate players and provides native chat-form fallbacks instead of clunky Java inventory screens.
- **Live Reloading:** Hot-reload configuration, items, and languages without rebooting or wiping active booster timers.

## ⌨️ Commands
| Command | Aliases | Description |
|---|---|---|
| `/nb help` | `?` | Displays the help menu. |
| `/nb open` | `gui`, `menu` | Opens the Booster GUI. |
| `/nb active` | `list` | View currently active boosters affecting you. |
| `/nb activate <id>` | `use` | Activate a booster from your inventory. |
| `/nb give <player> <id> <amount>`| `add` | Admin command to grant booster tokens. |
| `/nb reload` | `rl` | Reload the configurations safely. |
| `/nb admin` | None | Open the backend admin menu. |

## 🔑 Permissions
- `nexusboosters.use` : Required to access the GUI, view active boosters, and activate tokens.
- `nexusboosters.give` : Required to give booster tokens to players.
- `nexusboosters.reload` : Required to safely hot-reload the plugin.
- `nexusboosters.admin` : Required to access the admin UI.

## 📦 Dependencies
- **Required:** None! The plugin functions 100% autonomously out of the box.

## 🔗 Optional Hooks
- **PlaceholderAPI**: Provides live statistics like `%nexusboosters_multiplier_xp%` or `%nexusboosters_time_left_<id>%`.
- **Vault**: Exposes the native `MONEY` multiplier to compatible Vault economy systems.
- **Floodgate**: Enables seamless Bedrock Edition UX fallbacks.

## 🚀 Installation Steps
1. Download `NexusBoosters-1.0.0.jar`.
2. Place the jar file inside your server's `plugins` folder.
3. Start the server to generate the default configuration files.
4. (Optional) Install PlaceholderAPI, Vault, and Geyser-Floodgate for maximum feature integration.

## 📁 Configuration Files
- `config.yml` - Manages backend settings (like storage type).
- `boosters.yml` - Define all your custom boosters, durations, and multipliers here.
- `gui.yml` - Fully customize sizes, slots, materials, and names of the internal GUIs.
- `messages.yml` - Customize all chat messages, warnings, and success notifications.

## ⚠️ Known Limitations
- MySQL is not supported in `v1.0.0`; the default storage is optimized SQLite.
- Standard Bukkit/Spigot `BlockBreakEvent` multipliers are bypassed in favor of Paper's native `BlockDropItemEvent` to prevent severe item duplication exploits. This enforces the Paper 1.21.11 requirement.

## 📝 Changelog v1.0.0
- Initial full release.
- Implemented asynchronous SQLite database infrastructure.
- Added native memory caching for O(1) booster logic and O(N) multiplier fetching.
- Added GUI framework with smart Bedrock detection.
- Added `XP`, `Block Drop`, and `Mob Drop` multiplier listeners.
- Added Vault, PlaceholderAPI, and Floodgate optional hooks.
