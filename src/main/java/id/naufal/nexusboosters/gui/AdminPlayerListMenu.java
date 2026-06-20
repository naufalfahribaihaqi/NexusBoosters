package id.naufal.nexusboosters.gui;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.util.ItemBuilder;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class AdminPlayerListMenu implements NexusMenu {
    private final NexusBoostersPlugin plugin;
    private final Player player;
    private final int page;
    private final Inventory inventory;
    private final List<Player> onlinePlayers;

    public AdminPlayerListMenu(NexusBoostersPlugin plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        this.onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        this.inventory = Bukkit.createInventory(this, 54, TextUtil.color("&c&lAdmin - Player List"));
        setupItems();
    }

    private void setupItems() {
        int startIndex = page * 45;
        int slot = 0;

        for (int i = startIndex; i < Math.min(startIndex + 45, onlinePlayers.size()); i++) {
            Player target = onlinePlayers.get(i);
            
            ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
                    .nameComponent(TextUtil.color("&b" + target.getName()))
                    .lore(List.of("&7Click to view and manage", "&7this player's boosters."))
                    .build();
            
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                head.setItemMeta(meta);
            }

            inventory.setItem(slot++, head);
        }

        if (page > 0) {
            inventory.setItem(48, new ItemBuilder(Material.ARROW).nameComponent(TextUtil.color("&ePrevious Page")).build());
        }
        if (startIndex + 45 < onlinePlayers.size()) {
            inventory.setItem(50, new ItemBuilder(Material.ARROW).nameComponent(TextUtil.color("&eNext Page")).build());
        }

        inventory.setItem(49, new ItemBuilder(Material.BARRIER).nameComponent(TextUtil.color("&cBack")).build());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if (slot == 49) {
            plugin.getGuiService().openAdminMenu(player);
        } else if (slot == 48 && page > 0) {
            plugin.getGuiService().openAdminPlayerListMenu(player, page - 1);
        } else if (slot == 50 && (page + 1) * 45 < onlinePlayers.size()) {
            plugin.getGuiService().openAdminPlayerListMenu(player, page + 1);
        } else if (slot >= 0 && slot < 45) {
            int index = (page * 45) + slot;
            if (index < onlinePlayers.size()) {
                Player target = onlinePlayers.get(index);
                plugin.getGuiService().openAdminPlayerBoosterMenu(player, target, 0);
            }
        }
    }
}
