# Configuration Examples

## `config.yml`
```yaml
storage:
  # Type of storage. Only "sqlite" is supported currently.
  type: "sqlite"
```

## `boosters.yml`
```yaml
boosters:
  money_2x:
    display-name: "&a2x Money Booster"
    type: MONEY
    scope: GLOBAL
    multiplier: 2.0
    duration-seconds: 3600
    material: EMERALD
    permission: "nexusboosters.booster.money_2x"

  xp_1_5x:
    display-name: "&b1.5x XP Booster"
    type: XP
    scope: PERSONAL
    multiplier: 1.5
    duration-seconds: 1800
    material: EXPERIENCE_BOTTLE
```

## `gui.yml`
```yaml
booster-menu:
  title: "&8Available Boosters"
  size: 54
  items:
    booster-format:
      lore:
        - "&7Scope: &f{scope}"
        - "&7Multiplier: &a{multiplier}x"
        - "&7Duration: &e{duration}s"
        - ""
        - "&aClick to activate!"
    close:
      material: BARRIER
      name: "&cClose Menu"
      slot: 53
```

## `messages.yml`
```yaml
prefix: "&8[&bNexusBoosters&8] &7"
give-success: "&aSuccessfully gave {amount}x {booster} to {player}."
activate-success: "&aSuccessfully activated {booster}!"
```
