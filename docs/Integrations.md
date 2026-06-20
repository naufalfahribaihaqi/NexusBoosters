# Integrations

Enable native third-party hooks within `config.yml`.

## Overview

NexusBoosters integrates deeply with established plugins. Toggle these in the `hooks` configuration section.

## Vault

* **Purpose:** Enables `MONEY` booster types.
* **Required Configuration:** `hooks.vault: true`
* **Unavailable Behavior:** Money boosters will fail to initialize and will not boost economy transactions.

## PlaceholderAPI

* **Purpose:** Exposes multipliers and duration variables.
* **Required Configuration:** `hooks.placeholderapi: true`
* **Unavailable Behavior:** Placeholders will not parse in menus or chat plugins.

## Floodgate

* **Purpose:** Detects Bedrock players and disables complex GUIs.
* **Required Configuration:** `hooks.floodgate: true`
* **Unavailable Behavior:** Bedrock players may crash or fail to interact with standard inventory menus.

## PlayerPoints

* **Purpose:** Enables `PLAYERPOINTS_GAIN` booster types.
* **Required Configuration:** `hooks.playerpoints: true`
* **Unavailable Behavior:** PlayerPoints boosters will do nothing.

## ShopGUI+

* **Purpose:** Enables `SHOP_BUY_DISCOUNT`, `SHOP_SELL`, and `SHOP_CATEGORY_SELL` booster types.
* **Required Configuration:** `hooks.shopguiplus: true`
* **Unavailable Behavior:** Shop prices will remain unaffected.

---

[← Previous](Cross-Server-Setup) | [Home](Home) | [Next →](Placeholders)
