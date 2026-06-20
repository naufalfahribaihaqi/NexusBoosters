# Boosters Configuration

The `boosters.yml` file is where you define every booster type available on your server.

## Booster Structure

Every booster entry looks like this:

```yaml
booster_id_here:
  type: <TYPE>
  scope: <SCOPE>
  multiplier: <NUMBER>
  duration: <SECONDS>
  material: <MATERIAL>
  name: "<NAME>"
  lore:
    - "<LORE_LINE_1>"
  permission: "<OPTIONAL_PERMISSION>"
```

### Available Types
NexusBoosters strictly supports the following implementations:
*   `MONEY` (Requires Vault)
*   `XP` (Vanilla Experience)
*   `BLOCK_DROPS` (Vanilla Block Breaking)
*   `MOB_DROPS` (Vanilla Mob Kills)
*   `PLAYERPOINTS_GAIN` (Requires PlayerPoints)
*   `SHOP_BUY_DISCOUNT` (Requires ShopGUI+)
*   `SHOP_SELL` (Requires ShopGUI+)
*   `SHOP_CATEGORY_SELL` (Requires ShopGUI+)
*   `CUSTOM` (Does nothing inherently, useful for custom API implementations)

### Available Scopes
*   `PERSONAL`: Only affects the player who activated it.
*   `GLOBAL`: Affects everyone currently on the server.
*   `SERVER`: Affects the entire server environment (functions identically to `GLOBAL` in basic setups).

## Examples

**Personal XP Booster**
```yaml
weekend_xp:
  type: XP
  scope: PERSONAL
  multiplier: 2.0
  duration: 3600 # 1 Hour
  material: EXPERIENCE_BOTTLE
  name: "&aDouble XP Booster"
  lore:
    - "&7Grants 2x XP for 1 hour."
```

**Global Money Booster (Requires Vault)**
```yaml
global_money:
  type: MONEY
  scope: GLOBAL
  multiplier: 1.5
  duration: 7200 # 2 Hours
  material: GOLD_INGOT
  name: "&eGlobal 1.5x Economy"
  permission: "nexusboosters.booster.vip"
```

## Stacking Behavior
By default, the plugin uses `HIGHEST_ONLY` stacking mode. If a player activates a `1.5x` money booster and a `2.0x` money booster is active, the player will only receive a `2.0x` multiplier total. 
You can allow global and personal boosters to stack in `config.yml` via the `allow-global-and-personal-stack` option.
