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
        this.inventory = Bukkit.createInventory(this, 27, TextUtil.color("&c&lAdmin Menu"));

        inventory.setItem(11, new ItemBuilder(Material.BEACON)
                .nameComponent(TextUtil.color("&bActive Boosters"))
                .lore(List.of("&7View and manage all active boosters", "", "&eClick to open!"))
                .build());

        inventory.setItem(15, new ItemBuilder(Material.PLAYER_HEAD)
                .nameComponent(TextUtil.color("&ePlayer Manager"))
                .lore(List.of("&7View online players and their boosters", "", "&eClick to open!"))
                .build());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot == 11) {
            plugin.getGuiService().openAdminActiveMenu(player, 0);
        } else if (slot == 15) {
            plugin.getGuiService().openAdminPlayerListMenu(player, 0);
        }
    }
}
