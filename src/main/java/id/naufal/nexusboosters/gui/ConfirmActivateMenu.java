package id.naufal.nexusboosters.gui;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.util.ItemBuilder;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConfirmActivateMenu implements NexusMenu {
    private final NexusBoostersPlugin plugin;
    private final Player player;
    private final id.naufal.nexusboosters.player.PlayerBoosterToken token;
    private final Inventory inventory;

    public ConfirmActivateMenu(NexusBoostersPlugin plugin, Player player, id.naufal.nexusboosters.player.PlayerBoosterToken token) {
        this.plugin = plugin;
        this.player = player;
        this.token = token;

        FileConfiguration config = plugin.getGuiConfig().getConfig();
        String title = config.getString("confirm-menu.title", "&8Confirm Activation?");
        int size = config.getInt("confirm-menu.size", 27);
        
        this.inventory = Bukkit.createInventory(this, size, TextUtil.color(title));
        setupItems(config);
    }

    private void setupItems(FileConfiguration config) {
        inventory.setItem(config.getInt("confirm-menu.items.confirm.slot", 11), 
                createItem(config, "confirm-menu.items.confirm"));
        inventory.setItem(config.getInt("confirm-menu.items.cancel.slot", 15), 
                createItem(config, "confirm-menu.items.cancel"));
        
        ItemStack info = new ItemBuilder(Material.PAPER)
                .nameComponent(TextUtil.color("&b" + token.getBoosterId()))
                .build();
        inventory.setItem(config.getInt("confirm-menu.items.info.slot", 13), info);
    }

    private ItemStack createItem(FileConfiguration config, String path) {
        Material mat = Material.matchMaterial(config.getString(path + ".material", "STONE"));
        if (mat == null) mat = Material.STONE;
        ItemBuilder builder = new ItemBuilder(mat)
                .nameComponent(TextUtil.color(config.getString(path + ".name", "")));
        
        List<String> loreStr = config.getStringList(path + ".lore");
        if (!loreStr.isEmpty()) {
            builder.lore(loreStr);
        }
        return builder.build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        FileConfiguration config = plugin.getGuiConfig().getConfig();
        
        if (slot == config.getInt("confirm-menu.items.cancel.slot", 15)) {
            plugin.getGuiService().openBoosterMenu(player, 0);
        } else if (slot == config.getInt("confirm-menu.items.confirm.slot", 11)) {
            player.closeInventory();
            boolean success = plugin.getBoosterManager().tryActivateBooster(player, token, plugin);
            if (plugin.getConfig().getBoolean("gui-debug", false)) {
                plugin.getLogger().info("[GUI Debug] Player " + player.getName() + " clicked Confirm for booster " + token.getBoosterId() + " at slot " + slot + ", success: " + success);
            }
        }
    }
}
