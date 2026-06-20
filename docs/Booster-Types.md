# Booster Types

NexusBoosters provides several native multipliers that hook directly into server events or third-party plugins.

## Overview

You must define the `type` in `boosters.yml`. If an integration is missing, that booster type will safely do nothing.

## Available Types

| Type | Effect | Required Integration |
| ---- | ------ | -------------------- |
| `MONEY` | Multiplies economy transactions. | Vault |
| `XP` | Multiplies vanilla experience drops. | None |
| `BLOCK_DROPS` | Multiplies drops from broken blocks. | None |
| `MOB_DROPS` | Multiplies drops from killed mobs. | None |
| `PLAYERPOINTS_GAIN` | Multiplies PlayerPoints rewards. | PlayerPoints |
| `SHOP_BUY_DISCOUNT` | Reduces buy prices. | ShopGUI+ |
| `SHOP_SELL` | Increases sell prices. | ShopGUI+ |
| `SHOP_CATEGORY_SELL` | Increases sell prices in categories. | ShopGUI+ |
| `CUSTOM` | No internal effect. For developer API usage. | None |

## Notes

> Booster types not listed here are not implemented and will not function.

---

[← Previous](Booster-Configuration) | [Home](Home) | [Next →](Commands)
