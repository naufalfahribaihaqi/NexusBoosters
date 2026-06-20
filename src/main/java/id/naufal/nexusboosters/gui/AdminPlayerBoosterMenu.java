package id.naufal.nexusboosters.gui;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.Booster;
import id.naufal.nexusboosters.player.PlayerBoosterToken;
import id.naufal.nexusboosters.player.PlayerData;
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
import java.util.Map;

public class AdminPlayerBoosterMenu implements NexusMenu {
    private final NexusBoostersPlugin plugin;
    private final Player admin;
    private final Player target;
    private final int page;
    private final Inventory inventory;
    private final List<Map.Entry<PlayerBoosterToken, Integer>> playerBoosters;

    public AdminPlayerBoosterMenu(NexusBoostersPlugin plugin, Player admin, Player target, int page) {
        this.plugin = plugin;
        this.admin = admin;
        this.target = target;
        this.page = page;

        PlayerData data = plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        this.playerBoosters = new ArrayList<>(data.getBoosterInventory().entrySet());

        this.inventory = Bukkit.createInventory(this, 54, TextUtil.color("&c&lAdmin - " + target.getName()));
        setupItems();
    }

    private void setupItems() {
        int startIndex = page * 45;
        int slot = 0;

        for (int i = startIndex; i < Math.min(startIndex + 45, playerBoosters.size()); i++) {
            Map.Entry<PlayerBoosterToken, Integer> entry = playerBoosters.get(i);
            PlayerBoosterToken token = entry.getKey();
            int amount = entry.getValue();
            
            Booster booster = plugin.getBoosterRegistry().getBooster(token.getBoosterId());
            String displayName = booster != null ? booster.getDisplayName() : token.getBoosterId();

            List<String> lore = new ArrayList<>();
            lore.add("&7Type: &f" + token.getScope().name());
            lore.add("&7Amount: &a" + amount);
            if (token.getDurationOverrideSeconds() > 0) {
                lore.add("&7Duration: &e" + TextUtil.formatTime(token.getDurationOverrideSeconds()));
            } else if (booster != null) {
                lore.add("&7Duration: &e" + TextUtil.formatTime(booster.getDurationSeconds()) + " &8(Default)");
            }
            lore.add("");
            lore.add("&c[Shift-Right-Click] &7to remove 1.");
            lore.add("&c[Shift-Left-Click] &7to clear all.");

            Material mat = Material.CHEST;
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
            inventory.setItem(48, new ItemBuilder(Material.ARROW).nameComponent(TextUtil.color("&ePrevious Page")).build());
        }
        if (startIndex + 45 < playerBoosters.size()) {
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
            plugin.getGuiService().openAdminPlayerListMenu(admin, 0);
        } else if (slot == 48 && page > 0) {
            plugin.getGuiService().openAdminPlayerBoosterMenu(admin, target, page - 1);
        } else if (slot == 50 && (page + 1) * 45 < playerBoosters.size()) {
            plugin.getGuiService().openAdminPlayerBoosterMenu(admin, target, page + 1);
        } else if (slot >= 0 && slot < 45) {
            int index = (page * 45) + slot;
            if (index < playerBoosters.size()) {
                Map.Entry<PlayerBoosterToken, Integer> entry = playerBoosters.get(index);
                PlayerBoosterToken token = entry.getKey();
                PlayerData data = plugin.getPlayerManager().getPlayerData(target.getUniqueId());

                if (event.getClick().isShiftClick() && event.getClick().isRightClick()) {
                    data.takeBooster(token, 1);
                    plugin.getStorageService().savePlayerData(data);
                    plugin.getGuiService().openAdminPlayerBoosterMenu(admin, target, page);
                } else if (event.getClick().isShiftClick() && event.getClick().isLeftClick()) {
                    data.setBoosterAmount(token, 0);
                    plugin.getStorageService().savePlayerData(data);
                    plugin.getGuiService().openAdminPlayerBoosterMenu(admin, target, page);
                }
            }
        }
    }
}
