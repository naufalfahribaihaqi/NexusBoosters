package id.naufal.nexusboosters.listener;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.BoosterType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class MobDropListener implements Listener {
    private final NexusBoostersPlugin plugin;

    public MobDropListener(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        double multiplier = plugin.getBoosterManager().getActiveMultiplier(killer.getUniqueId(), BoosterType.MOB_DROPS);
        if (multiplier <= 1.0) return;

        for (ItemStack drop : event.getDrops()) {
            int newAmount = (int) (drop.getAmount() * multiplier);
            drop.setAmount(newAmount);
        }
    }
}
