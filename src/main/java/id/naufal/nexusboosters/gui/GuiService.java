package id.naufal.nexusboosters.gui;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiService implements Listener {
    private final NexusBoostersPlugin plugin;

    public GuiService(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openBoosterMenu(Player player, int page) {
        if (plugin.getHookManager().getFloodgateHook().isBedrockPlayer(player.getUniqueId())) {
            openBedrockBoosterMenu(player);
            return;
        }
        player.openInventory(new BoosterMenu(plugin, player, page).getInventory());
    }

    public void openActiveBoosterMenu(Player player, int page) {
        if (plugin.getHookManager().getFloodgateHook().isBedrockPlayer(player.getUniqueId())) {
            openBedrockActiveMenu(player);
            return;
        }
        player.openInventory(new ActiveBoosterMenu(plugin, player, page).getInventory());
    }

    public void openConfirmMenu(Player player, id.naufal.nexusboosters.player.PlayerBoosterToken token) {
        if (plugin.getHookManager().getFloodgateHook().isBedrockPlayer(player.getUniqueId())) {
            // Bedrock fallback logic for confirmation
            player.sendMessage("§e[NexusBoosters] §7Type §b/nb activate " + token.getBoosterId() + " §7to confirm.");
            return;
        }
        player.openInventory(new ConfirmActivateMenu(plugin, player, token).getInventory());
    }

    private void openBedrockBoosterMenu(Player player) {
        player.sendMessage("§e[NexusBoosters] §7Available Boosters:");
        for (id.naufal.nexusboosters.booster.Booster b : plugin.getBoosterRegistry().getAllBoosters()) {
            player.sendMessage("§7- §b" + b.getDisplayName() + " §8(ID: " + b.getId() + ")");
        }
        player.sendMessage("§7Use §e/nb activate <id> §7to activate.");
    }

    private void openBedrockActiveMenu(Player player) {
        plugin.getServer().dispatchCommand(player, "nb active");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof NexusMenu) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            ((NexusMenu) event.getInventory().getHolder()).onClick(event);
        }
    }

    public void openAdminMenu(Player player) {
        if (plugin.getHookManager().getFloodgateHook().isBedrockPlayer(player.getUniqueId())) {
            player.sendMessage("§e[NexusBoosters] §cAdmin menu not available on Bedrock UI.");
            return;
        }
        player.openInventory(new AdminMenu(plugin, player).getInventory());
    }

    public void openAdminActiveMenu(Player player, int page) {
        player.openInventory(new AdminActiveMenu(plugin, player, page).getInventory());
    }

    public void openAdminPlayerListMenu(Player player, int page) {
        player.openInventory(new AdminPlayerListMenu(plugin, player, page).getInventory());
    }

    public void openAdminPlayerBoosterMenu(Player player, Player target, int page) {
        player.openInventory(new AdminPlayerBoosterMenu(plugin, player, target, page).getInventory());
    }
}
