package id.naufal.nexusboosters.gui;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.Booster;
import id.naufal.nexusboosters.util.ItemBuilder;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AdminActiveMenu implements NexusMenu {
    private final NexusBoostersPlugin plugin;
    private final Player player;
    private final int page;
    private final Inventory inventory;
    private final List<ActiveBooster> activeBoosters;

    public AdminActiveMenu(NexusBoostersPlugin plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;

        this.activeBoosters = new ArrayList<>();
        for (ActiveBooster ab : plugin.getBoosterManager().getAllActiveBoosters()) {
            if (!ab.isExpired()) {
                this.activeBoosters.add(ab);
            }
        }

        String title = plugin.getGuiConfig().getConfig().getString("admin-active-menu.title", "&8Admin - Active Boosters");
        int size = plugin.getGuiConfig().getConfig().getInt("admin-active-menu.size", 54);
        this.inventory = Bukkit.createInventory(this, size, TextUtil.color(title));
        setupItems();
    }

    private void setupItems() {
        int startIndex = page * 45;
        int slot = 0;

        for (int i = startIndex; i < Math.min(startIndex + 45, activeBoosters.size()); i++) {
            ActiveBooster ab = activeBoosters.get(i);
            Booster booster = plugin.getBoosterRegistry().getBooster(ab.getBoosterId());
            long remainingSec = (ab.getExpiresAt() - System.currentTimeMillis()) / 1000;
            if (remainingSec < 0) remainingSec = 0;

            String displayName = booster != null ? booster.getDisplayName() : ab.getBoosterId();
            double multiplier = booster != null ? booster.getMultiplier() : 1.0;

            List<String> lore = new ArrayList<>();
            lore.add("&7Type: &f" + ab.getScope().name());
            lore.add("&7Effect: &a" + multiplier + "x");
            if (ab.getOwnerUuid() != null) {
                Player owner = Bukkit.getPlayer(ab.getOwnerUuid());
                lore.add("&7Owner: &f" + (owner != null ? owner.getName() : ab.getOwnerUuid().toString()));
            } else {
                lore.add("&7Owner: &bServer (Global)");
            }
            lore.add("&7Remaining: &e" + TextUtil.formatTime(remainingSec));
            lore.add("");
            lore.add("&c[Shift-Right-Click] &7to STOP booster.");

            Material mat = Material.BEACON;
            if (booster != null) {
                Material bMat = Material.matchMaterial(booster.getMaterial());
                if (bMat != null) mat = bMat;
            }

            ItemStack item = new ItemBuilder(mat)
                    .nameComponent(TextUtil.color("&b" + displayName))
                    .lore(lore)
                    .build();

            inventory.setItem(slot++, item);
        }

        if (page > 0) {
            setupNavItem("prev-page", 48, Material.ARROW);
        }
        if (startIndex + 45 < activeBoosters.size()) {
            setupNavItem("next-page", 50, Material.ARROW);
        }

        setupNavItem("back", 49, Material.BARRIER);
    }

    private void setupNavItem(String key, int defaultSlot, Material defaultMat) {
        String path = "admin-active-menu.items." + key + ".";
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

        if (slot == plugin.getGuiConfig().getConfig().getInt("admin-active-menu.items.back.slot", 49)) {
            plugin.getGuiService().openAdminMenu(player);
        } else if (slot == plugin.getGuiConfig().getConfig().getInt("admin-active-menu.items.prev-page.slot", 48) && page > 0) {
            plugin.getGuiService().openAdminActiveMenu(player, page - 1);
        } else if (slot == plugin.getGuiConfig().getConfig().getInt("admin-active-menu.items.next-page.slot", 50) && (page + 1) * 45 < activeBoosters.size()) {
            plugin.getGuiService().openAdminActiveMenu(player, page + 1);
        } else if (slot >= 0 && slot < 45) {
            int index = (page * 45) + slot;
            if (index < activeBoosters.size()) {
                ActiveBooster ab = activeBoosters.get(index);
                if (event.getClick().isShiftClick() && event.getClick().isRightClick()) {
                    // Stop it
                    plugin.getStorageService().removeActiveBooster(ab.getBoosterId(), ab.getOwnerUuid());
                    if (plugin.getBossBarManager() != null) {
                        plugin.getBossBarManager().removeBossBar(ab.getBoosterId());
                    }
                    plugin.getStorageService().loadActiveBoosters().thenAccept(dbBoosters -> {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            plugin.getBoosterManager().syncFromDatabase(dbBoosters);
                            plugin.getGuiService().openAdminActiveMenu(player, page);
                        });
                    });
                }
            }
        }
    }
}
