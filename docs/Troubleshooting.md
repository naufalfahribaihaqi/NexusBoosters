# Troubleshooting

Resolve common issues efficiently.

## Plugin does not start

**Cause**

The server environment is outdated.

**Solution**

1. Verify your server is running Java 21.
2. Verify you are using Paper 1.21.x.
3. Check the console log for specific startup errors.

## GUI does not open

**Cause**

A syntax error exists in `gui.yml`.

**Solution**

1. Validate your `gui.yml` using a YAML checker.
2. Ensure layout matrix rows match the defined inventory size.
3. Check console for parsing exceptions.

## Economy booster does not work

**Cause**

Vault is missing or the hook is disabled.

**Solution**

1. Install Vault and a compatible economy engine.
2. Ensure `hooks.vault` is set to `true`.
3. Restart the server.

## SQLite database locked

**Cause**

The server was forcefully terminated, leaving connections open.

**Solution**

1. Perform a clean server stop.
2. Avoid using the Bukkit `/reload` command.

## Cross-server not syncing

**Cause**

Servers are isolated or misconfigured.

**Solution**

1. Ensure all servers use the exact same MySQL database credentials.
2. Ensure `server-id` is strictly unique per server instance.
3. Check if `sync-interval-seconds` delay has passed.

---

[← Previous](Placeholders) | [Home](Home) | [Next →](Developer-Guide)
