# Messages

Configure all text strings sent to players.

## Overview

The `messages.yml` file separates messages by function. You can use standard formatting codes.

## Categories

### General
| Key | Description | Available Variables |
| --- | ----------- | ------------------- |
| `prefix` | Global chat prefix. | None |
| `no-permission` | Sent when lacking permissions. | None |

### Booster Activation
| Key | Description | Available Variables |
| --- | ----------- | ------------------- |
| `activated-personal` | Sent upon activation. | `{booster_name}`, `{duration}` |
| `activated-global` | Broadcast to all players. | `{player}`, `{booster_name}` |
| `already-active` | Sent if booster type is running. | None |

### Admin
| Key | Description | Available Variables |
| --- | ----------- | ------------------- |
| `given-booster` | Admin receives confirmation. | `{player}`, `{amount}` |
| `received-booster` | Player receives tokens. | `{amount}`, `{booster_name}` |

### Errors
| Key | Description | Available Variables |
| --- | ----------- | ------------------- |
| `player-not-found` | Sent if target is offline. | None |
| `invalid-booster` | Sent if ID does not exist. | None |

---

[← Previous](GUI-Configuration) | [Home](Home) | [Next →](Database)
