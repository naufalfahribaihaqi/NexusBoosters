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

        String title = plugin.getGuiConfig().getConfig().getString("admin-player-menu.title", "&8Admin - Players");
        int size = plugin.getGuiConfig().getConfig().getInt("admin-player-menu.size", 54);
        this.inventory = Bukkit.createInventory(this, size, TextUtil.color(title));
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
            setupNavItem("prev-page", 48, Material.ARROW);
        }
        if (startIndex + 45 < onlinePlayers.size()) {
            setupNavItem("next-page", 50, Material.ARROW);
        }

        setupNavItem("back", 49, Material.BARRIER);
    }

    private void setupNavItem(String key, int defaultSlot, Material defaultMat) {
        String path = "admin-player-menu.items." + key + ".";
        Material mat = Material.matchMaterial(plugin.getGuiConfig().getConfig().getString(path + "material", defaultMat.name()));
        if (mat == null) mat = defaultMat;

        int slot = plugin.getGuiConfig().getConfig().getInt(path + "slot", defaultSlot);
        String name = plugin.getGuiConfig().getConfig().getString(path + "name", "&e" + key);

        inventory.setItem(slot, new ItemBuilder(mat).nameComponent(TextUtil.color(name)).build());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if (slot == plugin.getGuiConfig().getConfig().getInt("admin-player-menu.items.back.slot", 49)) {
            plugin.getGuiService().openAdminMenu(player);
        } else if (slot == plugin.getGuiConfig().getConfig().getInt("admin-player-menu.items.prev-page.slot", 48) && page > 0) {
            plugin.getGuiService().openAdminPlayerListMenu(player, page - 1);
        } else if (slot == plugin.getGuiConfig().getConfig().getInt("admin-player-menu.items.next-page.slot", 50) && (page + 1) * 45 < onlinePlayers.size()) {
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
