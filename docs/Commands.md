# Commands

A complete list of commands available to players and administrators.

## Overview

The main command is `/nexusboosters`, which can be aliased as `/nb` or `/booster`.

## Command List

| Command | Description | Permission | Console |
|---|---|---|---|
| `/nb help` | Displays the help menu. | `nexusboosters.use` | Yes |
| `/nb open` | Opens the Booster GUI. | `nexusboosters.use` | No |
| `/nb active` | View currently active boosters. | `nexusboosters.use` | No |
| `/nb activate <booster>` | Activate a booster by its ID. | `nexusboosters.use` | No |
| `/nb give <player> <booster> <amount> [scope] [duration]` | Grants booster tokens. | `nexusboosters.give` | Yes |
| `/nb startglobal <booster> [duration]` | Starts a global booster. | `nexusboosters.admin` | Yes |
| `/nb stopglobal <booster>` | Stops an active global booster. | `nexusboosters.admin` | Yes |
| `/nb sync` | Force database synchronization. | `nexusboosters.admin` | Yes |
| `/nb reload` | Hot-reloads all configs safely. | `nexusboosters.reload` | Yes |
| `/nb admin` | Opens backend admin menus. | `nexusboosters.admin` | No |

## Example

```bash
/nb give Steve weekend_xp 5 PERSONAL 7200
```

---

[← Previous](Booster-Types) | [Home](Home) | [Next →](Permissions)
