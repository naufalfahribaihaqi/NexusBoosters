# Cross-Server Setup

Synchronize booster activity across your entire network.

## Overview

You can share global multipliers and token inventories across multiple servers.

## Configuration Steps

1. Configure all servers to use the **same MySQL database**.
2. Set `cross-server.enabled` to `true`.
3. Set a unique `server-id` (e.g., `survival-1`) on every server.
4. Enable `global-boosters` and `player-balances` sync options as needed.
5. Restart all servers completely.
6. Activate a global booster on Server A and verify it appears on Server B.

## Notes

> Cross-server data is polled based on `sync-interval-seconds`. Expect a brief delay before remote changes update locally. Personal boosters do not sync across servers.

---

[← Previous](Database) | [Home](Home) | [Next →](Integrations)
