# Placeholders

NexusBoosters supports PlaceholderAPI. You can use these placeholders in other plugins (e.g., scoreboard, chat, menus) to display booster information. 

| Placeholder | Description |
| :--- | :--- |
| `%nexusboosters_active_global%` | Returns the total number of currently active global boosters on the server. |
| `%nexusboosters_active_personal%` | Returns the total number of personal boosters currently active for the player. |
| `%nexusboosters_multiplier_money%` | Returns the player's total current money multiplier (e.g., `1.50`). Defaults to `1.00` if no money boosters are active. |
| `%nexusboosters_multiplier_xp%` | Returns the player's total current XP multiplier. Defaults to `1.00`. |
| `%nexusboosters_time_left_<booster-id>%` | Returns the remaining time (in seconds) for a specific active booster by its ID (e.g., `%nexusboosters_time_left_weekend_xp%`). Returns `0` if not active. |

*Note: You do not need to download a separate ecloud expansion; the placeholders are registered internally by the plugin when PlaceholderAPI is detected.*
