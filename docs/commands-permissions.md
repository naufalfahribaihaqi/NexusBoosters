# Commands and Permissions

NexusBoosters provides an intuitive command structure for both players and administrators. 
The main command is `/nexusboosters`, which can be aliased as `/nb` or `/booster`.

## Commands

| Command | Aliases | Permission | Console Support | Description |
| :--- | :--- | :--- | :--- | :--- |
| `/nb help` | `?` | `nexusboosters.use` | Yes | Displays the help menu. |
| `/nb open` | `gui`, `menu` | `nexusboosters.use` | No | Opens the Booster GUI. Bedrock players get a text fallback. |
| `/nb active` | `list` | `nexusboosters.use` | No | View currently active boosters affecting you. |
| `/nb activate <booster>` | `use` | `nexusboosters.use` | No | Activate a booster by its ID. |
| `/nb give <player> <booster> <amount> [PERSONAL\|GLOBAL] [duration]` | `add` | `nexusboosters.give` | Yes | Grants booster tokens to a specific player's inventory. |
| `/nb startglobal <booster> [duration]` | `sg` | `nexusboosters.admin` | Yes | Start a global booster with an optional custom duration. |
| `/nb stopglobal <booster>` | none | `nexusboosters.admin` | Yes | Stop a specific active global booster. |
| `/nb sync` | none | `nexusboosters.admin` | Yes | Force cross-server synchronization (MySQL required). |
| `/nb reload` | `rl` | `nexusboosters.reload` | Yes | Hot-reloads all configs and languages safely. |
| `/nb admin` | none | `nexusboosters.admin` | No | Access the backend administrative menus. |

### Command Examples

**Grant a player 5 personal Money boosters:**
```bash
/nb give Steve money_booster 5 PERSONAL
```

**Start a 1-hour global XP booster from console:**
```bash
/nb startglobal xp_booster 3600
```

---

## Permissions

All permissions default to `op` unless otherwise specified by your permissions plugin (e.g., LuckPerms).

| Permission | Description | Default |
| :--- | :--- | :--- |
| `nexusboosters.use` | Standard player permission required to open the GUI, check active boosters, and use boosters. | `true` |
| `nexusboosters.give` | Admin permission allowing you to give booster tokens directly to players. | `op` |
| `nexusboosters.reload` | Admin permission to execute `/nb reload`. | `op` |
| `nexusboosters.admin` | Admin permission to view backend administrative menus and start/stop global boosters. | `op` |
| `nexusboosters.booster.<booster-id>` | **(Dynamic)** Specific permission required to activate individual boosters if `permission: ` is defined in `boosters.yml`. | `op` |

*Note: If you add `permission: nexusboosters.booster.vip` to a booster in `boosters.yml`, only players with that specific permission can activate it.*
