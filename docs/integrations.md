# Integrations

NexusBoosters is designed to work seamlessly alongside your favorite plugins. All integrations can be toggled in `config.yml`.

## Vault
**Purpose:** Required for Economy (`MONEY`) boosters.
**Behavior:** If Vault is not installed or `hooks.vault` is `false`, Money boosters will not function. NexusBoosters intercepts the economy transactions internally if supported by the economy provider, or provides multipliers via the API.

## PlaceholderAPI
**Purpose:** Allows using NexusBoosters data in scoreboards, chat, and other plugins.
**Behavior:** If installed, NexusBoosters automatically registers its placeholders. See [Placeholders](placeholders.md).

## Floodgate / Geyser
**Purpose:** Provides a fallback interface for Bedrock players.
**Behavior:** Bedrock players often struggle with complex Java inventory GUIs. If Floodgate is detected and `hooks.floodgate` is enabled, Bedrock players who run `/nb open` will receive a text-based, clickable fallback menu in their chat instead of an inventory GUI.

## PlayerPoints
**Purpose:** Required for `PLAYERPOINTS_GAIN` boosters.
**Behavior:** Allows players to earn extra PlayerPoints. If PlayerPoints is missing, this booster type will not work.

## ShopGUI+
**Purpose:** Required for shop-based boosters (`SHOP_BUY_DISCOUNT`, `SHOP_SELL`, `SHOP_CATEGORY_SELL`).
**Behavior:** Integrates with ShopGUIPlus pricing events. If disabled or missing, these booster types will not function.
