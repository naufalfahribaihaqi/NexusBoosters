# Configuration

NexusBoosters separates its configuration into four distinct files to keep things organized: `config.yml`, `boosters.yml`, `gui.yml`, and `messages.yml`.

## config.yml
This is the core settings file for the plugin.

*   `storage`: Configure your database connection. `type` can be `sqlite` or `mysql`. If using MySQL, fill out the `host`, `port`, `database`, `username`, and `password`.
*   `booster.expiry-check-interval-seconds`: How often the plugin sweeps for expired boosters. (Default: `30`)
*   `booster.stacking`: How multiple active boosters combine. (Default: `HIGHEST_ONLY`)
*   `booster.allow-global-and-personal-stack`: Whether a player can benefit from both a global and personal booster simultaneously. (Default: `true`)
*   `hooks`: Toggle individual integrations on or off (`vault`, `placeholderapi`, `floodgate`).
*   `bossbar`: Configuration for the on-screen BossBar for global boosters.
    *   `enabled`: Toggle the BossBar.
    *   `update-interval-ticks`: How often the text updates.
    *   `mode`: `ROTATE` (cycles through active boosters).
    *   `color`: Set colors for specific scopes or types (e.g., `GLOBAL: BLUE`).
    *   `style`: The segmentation style (e.g., `SEGMENTED_10`).
    *   `title-format`: The text format. Supports internal `%` variables.
*   `shopgui`: Settings specific to ShopGUI+ hooks.
*   `cross-server`: Configure network synchronization (Requires MySQL). See [Cross-Server](cross-server.md).

## boosters.yml
This file defines every booster available on your server. See [Boosters](boosters.md) for a complete guide on configuring boosters.

## gui.yml
Controls the layout and items of the `/nb open` graphical interface.
See [GUI](gui.md) for customization details.

## messages.yml
Contains all the text strings sent to players in chat. You can customize prefixes, error messages, and success notifications here. Color codes (legacy `&`) are supported.
