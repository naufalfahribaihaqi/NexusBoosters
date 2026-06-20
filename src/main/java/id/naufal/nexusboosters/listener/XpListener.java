package id.naufal.nexusboosters.listener;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.BoosterType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class XpListener implements Listener {
    private final NexusBoostersPlugin plugin;

    public XpListener(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onXpGain(PlayerExpChangeEvent event) {
        int amount = event.getAmount();
        if (amount <= 0) return;

        double multiplier = plugin.getBoosterManager().getActiveMultiplier(event.getPlayer().getUniqueId(), BoosterType.XP);
        if (multiplier <= 1.0) return;

        event.setAmount((int) (amount * multiplier));
    }
}
