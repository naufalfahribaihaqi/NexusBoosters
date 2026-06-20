# Installation

## Requirements
*   **Java Version:** Java 21 or higher.
*   **Server Software:** Paper 1.21.x (or forks like Purpur).

## Required Dependencies
*   None. NexusBoosters works out of the box for basic features like XP and Drop boosters.

## Optional Dependencies
*   **Vault:** For economy/money boosters.
*   **PlaceholderAPI:** For scoreboards and custom messages.
*   **Floodgate:** For Bedrock fallback menus.
*   **PlayerPoints:** For PlayerPoints boosters.
*   **ShopGUI+:** For shop discount/sell boosters.

## Installation Steps
1. Download the `NexusBoosters-1.0.0.jar` from the Releases page (or build it from source).
2. Stop your Minecraft server completely.
3. Place the `.jar` file into your server's `plugins` folder.
4. If you plan to use Vault, PlayerPoints, or ShopGUI+, ensure those plugins are also in the `plugins` folder.
5. Start your server.
6. The plugin will generate default configuration files in `plugins/NexusBoosters/`.
7. Configure the plugin to your liking (see [Configuration](configuration.md)).
8. If you made changes to `config.yml` or `boosters.yml`, you can safely run `/nb reload` from the console or in-game (requires admin permissions).

> [!CAUTION]
> While `/nb reload` is safe for updating messages and basic config values, if you are changing your database engine (e.g., from SQLite to MySQL), you **must** perform a full server restart.

## Updating the Plugin
1. Stop your server.
2. Delete the old `NexusBoosters.jar`.
3. Drop the new `.jar` file into the `plugins` directory.
4. Start the server. (The plugin will automatically warn you if your configuration files need manual updates).
