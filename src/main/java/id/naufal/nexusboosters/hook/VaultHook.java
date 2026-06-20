package id.naufal.nexusboosters.hook;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private final NexusBoostersPlugin plugin;
    private Economy econ = null;
    private boolean enabled = false;

    public VaultHook(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
        }
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            econ = rsp.getProvider();
            enabled = true;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Economy getEconomy() {
        return econ;
    }

    public double getMoneyMultiplier(org.bukkit.entity.Player player) {
        return plugin.getBoosterManager().getActiveMultiplier(player.getUniqueId(), id.naufal.nexusboosters.booster.BoosterType.MONEY);
    }
}
