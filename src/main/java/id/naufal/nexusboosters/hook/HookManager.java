package id.naufal.nexusboosters.hook;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import org.bukkit.Bukkit;

public class HookManager {
    private final NexusBoostersPlugin plugin;
    private VaultHook vaultHook;
    private FloodgateHook floodgateHook;
    private PlayerPointsHook playerPointsHook;
    private ShopGUIPlusHook shopGUIPlusHook;
    private boolean placeholderAPIEnabled = false;

    public HookManager(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        this.floodgateHook = new FloodgateHook();
        if (this.floodgateHook.isEnabled()) {
            plugin.getLogger().info("Hooked into Floodgate.");
        } else {
            plugin.getLogger().info("Floodgate not found, bedrock support disabled.");
        }

        this.vaultHook = new VaultHook(plugin);
        if (this.vaultHook.isEnabled()) {
            plugin.getLogger().info("Hooked into Vault Economy.");
        } else {
            plugin.getLogger().info("Vault not found or economy missing.");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook(plugin).register();
            placeholderAPIEnabled = true;
            plugin.getLogger().info("Hooked into PlaceholderAPI.");
        } else {
            plugin.getLogger().info("PlaceholderAPI not found.");
        }

        this.playerPointsHook = new PlayerPointsHook(plugin);
        this.playerPointsHook.init();

        this.shopGUIPlusHook = new ShopGUIPlusHook(plugin);
        this.shopGUIPlusHook.init();
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public FloodgateHook getFloodgateHook() {
        return floodgateHook;
    }

    public PlayerPointsHook getPlayerPointsHook() {
        return playerPointsHook;
    }

    public ShopGUIPlusHook getShopGUIPlusHook() {
        return shopGUIPlusHook;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }
}
