package id.naufal.nexusboosters.listener;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.BoosterType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class BlockDropListener implements Listener {
    private final NexusBoostersPlugin plugin;

    public BlockDropListener(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        double multiplier = plugin.getBoosterManager().getActiveMultiplier(player.getUniqueId(), BoosterType.BLOCK_DROPS);
        
        if (multiplier <= 1.0) return;

        for (Item item : event.getItems()) {
            ItemStack stack = item.getItemStack();
            int newAmount = (int) (stack.getAmount() * multiplier);
            stack.setAmount(newAmount);
            item.setItemStack(stack);
        }
    }
}
