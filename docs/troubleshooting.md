# Troubleshooting

If you encounter issues while using NexusBoosters, check these common solutions before opening a bug report.

## Plugin Fails to Load
*   **Error:** `UnsupportedClassVersionError`
    *   **Solution:** NexusBoosters requires **Java 21**. Update your server's Java version.
*   **Error:** `Could not pass event...` on startup.
    *   **Solution:** Ensure you are running Paper 1.21.x. Older versions are not supported.

## Economy Boosters Not Working
*   **Solution:** Ensure you have Vault installed and a compatible economy provider (like EssentialsX). Verify `hooks.vault` is `true` in `config.yml`.

## ShopGUI+ / PlayerPoints Boosters Not Working
*   **Solution:** Verify you have the correct dependencies installed. If `hooks.shopguiplus` or `hooks.playerpoints` is `false`, the plugin will intentionally ignore those booster types.

## SQLite Database Locked
*   **Error:** `[SQLite] database is locked`
    *   **Solution:** This typically happens if the server crashed or was forcefully killed, leaving the database connection open. A clean restart usually fixes this. Avoid using `/reload` in Bukkit.

## Cross-Server Sync Not Updating
*   **Solution:** 
    1. Ensure all servers are pointing to the exact same MySQL database credentials.
    2. Check that `cross-server.enabled` is `true`.
    3. Ensure no two servers share the same `server-id`.
    4. Verify that `cross-server.global-boosters` or `player-balances` are set to `true`.
    5. Note the delay: it takes up to `sync-interval-seconds` for external changes to pull.

## Boosters Disappear After Restart
*   **Solution:** Active boosters calculate their expiration using system time. If you stop the server for 3 hours, a 2-hour booster will expire while the server is offline. This is intended behavior.

## GUI Will Not Open
*   **Solution:** Check the console for errors regarding `gui.yml`. If you made a syntax error in the YAML matrix, the GUI cannot be built. Use a YAML validator.
