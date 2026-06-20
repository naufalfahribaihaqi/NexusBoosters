package id.naufal.nexusboosters.hook;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.BoosterType;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerPointsHook {
    private final NexusBoostersPlugin plugin;
    private boolean available = false;
    private PlayerPointsAPI api;

    public PlayerPointsHook(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            this.api = PlayerPoints.getInstance().getAPI();
            this.available = true;
            plugin.getLogger().info("PlayerPoints hook enabled.");
        } else {
            plugin.getLogger().info("PlayerPoints not found, hook disabled.");
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public PlayerPointsAPI getApi() {
        return api;
    }

    public int getPoints(UUID playerId) {
        if (!available || api == null) {
            return 0;
        }
        return api.look(playerId);
    }

    public boolean givePoints(UUID playerId, int amount) {
        if (!available || api == null) {
            return false;
        }
        return api.give(playerId, amount);
    }

    public boolean takePoints(UUID playerId, int amount) {
        if (!available || api == null) {
            return false;
        }
        return api.take(playerId, amount);
    }

    /**
     * API for other plugins to multiply their PlayerPoints rewards using NexusBoosters.
     */
    public double getPlayerPointsMultiplier(UUID playerId) {
        return plugin.getBoosterManager().getActiveMultiplier(playerId, BoosterType.PLAYERPOINTS_GAIN);
    }
}
