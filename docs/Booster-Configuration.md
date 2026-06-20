# Booster Configuration

Define available boosters within the `boosters.yml` file.

## Overview

The `boosters.yml` file maps internal keys to specific properties. Players cannot activate a booster type unless it exists here.

## Example

```yaml
boosters:
  example_booster:
    display-name: "&aExample Booster"
    type: XP
    scope: PERSONAL
    multiplier: 2.0
    duration-seconds: 3600
    material: EXPERIENCE_BOTTLE
    permission: "nexusboosters.booster.example_booster"
    requires-hooks: []
```

## Configuration

| Option | Type | Description | Example |
| ------ | ---- | ----------- | ------- |
| `display-name` | String | The name shown in the GUI. | `&aDouble XP` |
| `type` | String | The exact Booster Type. | `XP` |
| `scope` | String | The effect scope (`PERSONAL`, `GLOBAL`, `SERVER`). | `GLOBAL` |
| `multiplier` | Double | The boost multiplier amount. | `2.0` |
| `duration-seconds` | Integer | Total active duration in seconds. | `3600` |
| `material` | String | The item icon used in the GUI. | `DIAMOND` |
| `permission` | String | (Optional) Permission required to activate. | `vip.booster` |

---

[← Previous](Quick-Start) | [Home](Home) | [Next →](Booster-Types)
