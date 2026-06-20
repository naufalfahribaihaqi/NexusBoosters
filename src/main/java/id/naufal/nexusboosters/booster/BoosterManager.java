package id.naufal.nexusboosters.booster;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BoosterManager {
    private final Map<UUID, Map<String, ActiveBooster>> personalBoosters = new ConcurrentHashMap<>();
    private final Map<String, ActiveBooster> globalBoosters = new ConcurrentHashMap<>();
    private final BoosterRegistry registry;

    public BoosterManager(BoosterRegistry registry) {
        this.registry = registry;
    }

    public void activateBooster(ActiveBooster activeBooster) {
        if (activeBooster.getScope() == BoosterScope.PERSONAL && activeBooster.getOwnerUuid() != null) {
            personalBoosters.computeIfAbsent(activeBooster.getOwnerUuid(), k -> new ConcurrentHashMap<>())
                    .put(activeBooster.getBoosterId(), activeBooster);
        } else {
            globalBoosters.put(activeBooster.getBoosterId(), activeBooster);
        }
    }

    public boolean tryActivateBooster(org.bukkit.entity.Player player, String boosterId, id.naufal.nexusboosters.NexusBoostersPlugin plugin) {
        id.naufal.nexusboosters.player.PlayerData data = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        id.naufal.nexusboosters.player.PlayerBoosterToken token = data.findTokenById(boosterId);

        if (token == null) {
            Booster booster = registry.getBooster(boosterId);
            if (booster == null) {
                plugin.getMessageManager().sendMessage(player, "invalid-booster", "booster", boosterId);
            } else {
                plugin.getMessageManager().sendMessage(player, "activate-fail-no-balance");
            }
            return false;
        }

        return tryActivateBooster(player, token, plugin);
    }

    public boolean tryActivateBooster(org.bukkit.entity.Player player, id.naufal.nexusboosters.player.PlayerBoosterToken token, id.naufal.nexusboosters.NexusBoostersPlugin plugin) {
        Booster booster = registry.getBooster(token.getBoosterId());
        if (booster == null) {
            plugin.getMessageManager().sendMessage(player, "invalid-booster", "booster", token.getBoosterId());
            return false;
        }

        if (booster.getPermission() != null && !player.hasPermission(booster.getPermission())) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return false;
        }

        id.naufal.nexusboosters.player.PlayerData data = plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (data.takeBooster(token, 1)) {
            long now = System.currentTimeMillis();
            long duration = token.getDurationOverrideSeconds() > 0 ? token.getDurationOverrideSeconds() : booster.getDurationSeconds();
            long expiresAt = now + (duration * 1000L);
            ActiveBooster activeBooster = new ActiveBooster(token.getBoosterId(), player.getUniqueId(), token.getScope(), now, expiresAt);
            activateBooster(activeBooster);
            plugin.getStorageService().saveActiveBooster(activeBooster);
            plugin.getStorageService().savePlayerData(data);
            plugin.getMessageManager().sendMessage(player, "activate-success", "booster_name", booster.getDisplayName());
            
            if (plugin.getBossBarManager() != null) {
                plugin.getBossBarManager().updateOrAddBossBar(activeBooster);
            }
            return true;
        } else {
            plugin.getMessageManager().sendMessage(player, "activate-fail-no-balance");
            return false;
        }
    }

    public double getActiveMultiplier(UUID playerUuid, BoosterType type) {
        double multiplier = 1.0;

        for (ActiveBooster active : globalBoosters.values()) {
            if (!active.isExpired()) {
                Booster booster = registry.getBooster(active.getBoosterId());
                if (booster != null && booster.getType() == type) {
                    multiplier += (booster.getMultiplier() - 1.0);
                }
            } else {
                globalBoosters.remove(active.getBoosterId());
            }
        }

        if (playerUuid != null && personalBoosters.containsKey(playerUuid)) {
            Map<String, ActiveBooster> personal = personalBoosters.get(playerUuid);
            for (ActiveBooster active : personal.values()) {
                if (!active.isExpired()) {
                    Booster booster = registry.getBooster(active.getBoosterId());
                    if (booster != null && booster.getType() == type) {
                        multiplier += (booster.getMultiplier() - 1.0);
                    }
                } else {
                    personal.remove(active.getBoosterId());
                }
            }
        }

        return multiplier;
    }

    public void loadActiveBoosters(java.util.List<ActiveBooster> boosters) {
        for (ActiveBooster booster : boosters) {
            activateBooster(booster);
        }
    }

    public void syncFromDatabase(java.util.List<ActiveBooster> dbBoosters) {
        java.util.Set<String> validGlobalIds = new java.util.HashSet<>();
        
        for (ActiveBooster dbBooster : dbBoosters) {
            if (dbBooster.getScope() == BoosterScope.GLOBAL) {
                validGlobalIds.add(dbBooster.getBoosterId());
                globalBoosters.put(dbBooster.getBoosterId(), dbBooster);
            } else if (dbBooster.getScope() == BoosterScope.PERSONAL && dbBooster.getOwnerUuid() != null) {
                personalBoosters.computeIfAbsent(dbBooster.getOwnerUuid(), k -> new ConcurrentHashMap<>())
                        .put(dbBooster.getBoosterId(), dbBooster);
            }
        }

        globalBoosters.keySet().retainAll(validGlobalIds);
    }

    public java.util.List<ActiveBooster> getAllActiveBoosters() {
        java.util.List<ActiveBooster> all = new java.util.ArrayList<>();
        all.addAll(globalBoosters.values());
        for (Map<String, ActiveBooster> personal : personalBoosters.values()) {
            all.addAll(personal.values());
        }
        return all;
    }

    public void startExpiryTask(org.bukkit.plugin.Plugin plugin, id.naufal.nexusboosters.database.StorageService storage) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            globalBoosters.entrySet().removeIf(entry -> {
                if (entry.getValue().isExpired()) {
                    storage.removeActiveBooster(entry.getValue().getBoosterId(), null);
                    return true;
                }
                return false;
            });

            for (Map.Entry<UUID, Map<String, ActiveBooster>> playerEntry : personalBoosters.entrySet()) {
                playerEntry.getValue().entrySet().removeIf(entry -> {
                    if (entry.getValue().isExpired()) {
                        storage.removeActiveBooster(entry.getValue().getBoosterId(), playerEntry.getKey());
                        return true;
                    }
                    return false;
                });
            }
        }, 200L, 200L);
    }

    public Booster getEffectiveBooster(id.naufal.nexusboosters.player.PlayerBoosterToken token) {
        Booster base = registry.getBooster(token.getBoosterId());
        if (base == null) return null;
        BoosterScope effectiveScope = token.getScope() != null ? token.getScope() : base.getScope();
        int effectiveDuration = token.getDurationOverrideSeconds() > 0 ? token.getDurationOverrideSeconds() : base.getDurationSeconds();
        return new Booster(base.getId(), base.getDisplayName(), base.getType(), effectiveScope, base.getMultiplier(), effectiveDuration, base.getMaterial(), base.getPermission(), base.getRequiresHooks());
    }
}
