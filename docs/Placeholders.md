# Placeholders

Inject booster data into external plugins.

## Overview

You must have PlaceholderAPI installed and `hooks.placeholderapi` enabled in `config.yml`. The plugin registers its expansion internally; no separate ecloud download is needed.

## Available Placeholders

| Placeholder | Description | Example Output |
| ----------- | ----------- | -------------- |
| `%nexusboosters_active_global%` | Total active global boosters. | `2` |
| `%nexusboosters_active_personal%` | Total active personal boosters. | `1` |
| `%nexusboosters_multiplier_money%` | Current money multiplier. | `1.50` |
| `%nexusboosters_multiplier_xp%` | Current XP multiplier. | `2.00` |
| `%nexusboosters_time_left_<id>%` | Remaining seconds for a booster. | `3600` |

## Notes

> If no booster is active, multiplier placeholders default to `1.00`, and time placeholders return `0`.

---

[← Previous](Integrations) | [Home](Home) | [Next →](Troubleshooting)
