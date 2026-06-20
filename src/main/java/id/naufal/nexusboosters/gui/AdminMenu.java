package id.naufal.nexusboosters.gui;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.util.ItemBuilder;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class AdminMenu implements NexusMenu {
    private final NexusBoostersPlugin plugin;
    private final Player player;
    private final Inventory inventory;

    public AdminMenu(NexusBoostersPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        String title = plugin.getGuiConfig().getConfig().getString("admin-menu.title", "&8Admin Menu");
        int size = plugin.getGuiConfig().getConfig().getInt("admin-menu.size", 27);
        this.inventory = Bukkit.createInventory(this, size, TextUtil.color(title));

        setupItem("active", 11, Material.BEACON);
        setupItem("players", 15, Material.PLAYER_HEAD);
    }

    private void setupItem(String key, int defaultSlot, Material defaultMat) {
        String path = "admin-menu.items." + key + ".";
        Material mat = Material.matchMaterial(plugin.getGuiConfig().getConfig().getString(path + "material", defaultMat.name()));
        if (mat == null) mat = defaultMat;

        int slot = plugin.getGuiConfig().getConfig().getInt(path + "slot", defaultSlot);
        String name = plugin.getGuiConfig().getConfig().getString(path + "name", "&b" + key);
        List<String> lore = plugin.getGuiConfig().getConfig().getStringList(path + "lore");

        inventory.setItem(slot, new ItemBuilder(mat)
                .nameComponent(TextUtil.color(name))
                .lore(lore)
                .build());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == plugin.getGuiConfig().getConfig().getInt("admin-menu.items.active.slot", 11)) {
            plugin.getGuiService().openAdminActiveMenu(player, 0);
        } else if (slot == plugin.getGuiConfig().getConfig().getInt("admin-menu.items.players.slot", 15)) {
            plugin.getGuiService().openAdminPlayerListMenu(player, 0);
        }
    }
}
