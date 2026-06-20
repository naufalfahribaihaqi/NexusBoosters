# NexusBoosters

A production-ready, all-in-one Minecraft booster plugin carefully built with high performance and flexibility in mind.

## Features
- **Async Database Architecture**: Zero main-thread blocking. SQLite supported by default.
- **Multiple Booster Types**: XP, Money, Mob Drops, Block Drops.
- **Scope Support**: `PERSONAL` (only affects the activator) and `GLOBAL` (affects the entire server).
- **GUI Driven**: Pagination-supported, Floodgate-compliant Bedrock fallback GUI.
- **Optional Hooks**: Seamless integration with PlaceholderAPI, Vault, and Geyser/Floodgate.

## Installation
1. Drop the `NexusBoosters.jar` into your `plugins` folder.
2. (Optional) Install PlaceholderAPI, Vault, and an economy plugin.
3. Start the server. Configure `boosters.yml` and `config.yml`.
4. Use `/nb reload` to hot-swap configuration changes!
