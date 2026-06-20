# Cross-Server Synchronization

NexusBoosters allows you to synchronize booster data across multiple servers (e.g., Survival and Skyblock) connected to the same MySQL database.

## Requirements
- You **MUST** use MySQL. SQLite cannot synchronize across different servers.
- All servers participating in the sync must point to the same database credentials in `config.yml`.

## Configuration
In `config.yml`, locate the `cross-server` section:

```yaml
cross-server:
  enabled: true
  server-id: "survival-1"
  sync-interval-seconds: 5
  global-boosters: true
  player-balances: true
  bossbar-sync: true
```

### Options
*   `server-id`: A unique identifier for the server. **Do not use the same ID on two different servers.**
*   `sync-interval-seconds`: How often the plugin queries the database for external changes.
*   `global-boosters`: If `true`, a global booster activated on Server A will also activate on Server B.
*   `player-balances`: If `true`, players' booster token inventories are synced.
*   `bossbar-sync`: If `true`, cross-server global boosters will display the BossBar on all synchronized servers.

## Limitations
*   Due to the nature of polling, cross-server sync relies on the `sync-interval-seconds`. There may be a slight delay (e.g., up to 5 seconds) before a booster activated on Server A appears on Server B.
*   Personal boosters are not synced cross-server.
