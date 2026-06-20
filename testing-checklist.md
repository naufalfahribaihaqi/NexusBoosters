# Testing Checklist

### [ ] Phase 1 - 3: Core Database & Architecture
- [ ] Plugin loads without stacktraces.
- [ ] `database.db` generates inside the plugin directory.
- [ ] Active boosters are saved when the server shuts down.
- [ ] Active boosters survive server reboots perfectly.

### [ ] Phase 4: Command System
- [ ] `/nb give` successfully updates SQLite database asynchronously.
- [ ] Tab completion limits players based on their permissions.
- [ ] Activating a booster subtracts exactly 1 from the player's balance.

### [ ] Phase 5: GUI
- [ ] Bedrock players safely receive the Chat Interface fallback.
- [ ] Java players get the Bukkit Inventory GUI with accurate Placeholder translation.
- [ ] Changing GUI names in `gui.yml` correctly updates the physical GUI.

### [ ] Phase 6: Gameplay Multipliers
- [ ] `PlayerExpChangeEvent` actually grants visually multiplied XP.
- [ ] `BlockDropItemEvent` yields multiple items upon breaking naturally.
- [ ] `EntityDeathEvent` correctly drops multiplied loot only if a player killed the mob.

### [ ] Phase 7: Hooks
- [ ] `PlaceholderAPI` correctly resolves `%nexusboosters_multiplier_xp%`.
- [ ] Safe logic continues to execute if `Vault` or `Floodgate` is removed from plugins folder.
