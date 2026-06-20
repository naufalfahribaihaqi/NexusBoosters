package id.naufal.nexusboosters.hook;

import org.bukkit.Bukkit;
import java.util.UUID;

public class FloodgateHook {
    private boolean enabled = false;

    public FloodgateHook() {
        if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            enabled = true;
        }
    }

    public boolean isBedrockPlayer(UUID uuid) {
        if (!enabled) return false;
        return checkBedrock(uuid);
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    private boolean checkBedrock(UUID uuid) {
        try {
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        } catch (Throwable t) {
            return false;
        }
    }
}
