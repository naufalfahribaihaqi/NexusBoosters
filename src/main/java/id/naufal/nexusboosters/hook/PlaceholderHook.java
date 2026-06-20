package id.naufal.nexusboosters.hook;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.BoosterType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {
    private final NexusBoostersPlugin plugin;

    public PlaceholderHook(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "nexusboosters";
    }

    @Override
    public @NotNull String getAuthor() {
        return "naufal";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        if (params.equals("active_global")) {
            long count = plugin.getBoosterManager().getAllActiveBoosters().stream()
                    .filter(b -> b.getScope() == id.naufal.nexusboosters.booster.BoosterScope.GLOBAL && !b.isExpired())
                    .count();
            return String.valueOf(count);
        }

        if (params.equals("active_personal")) {
            long count = plugin.getBoosterManager().getAllActiveBoosters().stream()
                    .filter(b -> b.getScope() == id.naufal.nexusboosters.booster.BoosterScope.PERSONAL 
                            && b.getOwnerUuid() != null 
                            && b.getOwnerUuid().equals(player.getUniqueId()) 
                            && !b.isExpired())
                    .count();
            return String.valueOf(count);
        }

        if (params.equals("multiplier_money")) {
            return String.format("%.2f", plugin.getBoosterManager().getActiveMultiplier(player.getUniqueId(), BoosterType.MONEY));
        }

        if (params.equals("multiplier_xp")) {
            return String.format("%.2f", plugin.getBoosterManager().getActiveMultiplier(player.getUniqueId(), BoosterType.XP));
        }

        if (params.startsWith("time_left_")) {
            String boosterId = params.substring("time_left_".length());
            for (ActiveBooster active : plugin.getBoosterManager().getAllActiveBoosters()) {
                if (active.getBoosterId().equalsIgnoreCase(boosterId) && !active.isExpired()) {
                    if (active.getScope() == id.naufal.nexusboosters.booster.BoosterScope.GLOBAL || 
                       (active.getOwnerUuid() != null && active.getOwnerUuid().equals(player.getUniqueId()))) {
                        return String.valueOf((active.getExpiresAt() - System.currentTimeMillis()) / 1000);
                    }
                }
            }
            return "0";
        }

        return null;
    }
}
