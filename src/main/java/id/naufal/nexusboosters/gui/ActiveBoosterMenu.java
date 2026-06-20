package id.naufal.nexusboosters.gui;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.Booster;
import id.naufal.nexusboosters.util.ItemBuilder;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActiveBoosterMenu implements NexusMenu {
    private final NexusBoostersPlugin plugin;
    private final Player player;
    private final int page;
    private final Inventory inventory;
    private final List<ActiveBooster> activeBoosters;

    public ActiveBoosterMenu(NexusBoostersPlugin plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        
        this.activeBoosters = new ArrayList<>();
        for (ActiveBooster ab : plugin.getBoosterManager().getAllActiveBoosters()) {
            if (!ab.isExpired() && (ab.getOwnerUuid() == null || ab.getOwnerUuid().equals(player.getUniqueId()))) {
                this.activeBoosters.add(ab);
            }
        }

        FileConfiguration config = plugin.getGuiConfig().getConfig();
        String title = config.getString("active-menu.title", "\u00268Active Boosters");
        int size = config.getInt("active-menu.size", 54);
        
        this.inventory = Bukkit.createInventory(this, size, TextUtil.color(title));
        setupItems(config);
    }

    private void setupItems(FileConfiguration config) {
        int startIndex = page * 45;
        int slot = 0;
        
        for (int i = startIndex; i < Math.min(startIndex + 45, activeBoosters.size()); i++) {
            ActiveBooster ab = activeBoosters.get(i);
            Booster booster = plugin.getBoosterRegistry().getBooster(ab.getBoosterId());
            long remainingSec = (ab.getExpiresAt() - System.currentTimeMillis()) / 1000;
            if (remainingSec < 0) remainingSec = 0;
            
            String displayName = booster != null ? booster.getDisplayName() : ab.getBoosterId();
            double multiplier = booster != null ? booster.getMultiplier() : 1.0;
            String scopeName = ab.getScope().name();
            
            List<String> lore = new ArrayList<>();
            lore.add("&7Type: &f" + scopeName);
            lore.add("&7Effect: &a" + multiplier + "x");
            if (ab.getOwnerUuid() != null) {
                lore.add("&7Owner: &f" + player.getName());
            } else {
                lore.add("&7Owner: &bServer (Global)");
            }
            lore.add("&7Remaining: &e" + TextUtil.formatTime(remainingSec));
            lore.add("");
            lore.add("&eClick for details.");
            
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

        inventory.setItem(config.getInt("active-menu.items.back.slot", 49), 
                createItem(config, "active-menu.items.back"));
        inventory.setItem(config.getInt("active-menu.items.close.slot", 53), 
                createItem(config, "active-menu.items.close"));
    }

    private ItemStack createItem(FileConfiguration config, String path) {
        Material mat = Material.matchMaterial(config.getString(path + ".material", "STONE"));
        if (mat == null) mat = Material.STONE;
        return new ItemBuilder(mat)
                .nameComponent(TextUtil.color(config.getString(path + ".name", "")))
                .build();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        FileConfiguration config = plugin.getGuiConfig().getConfig();
        
        if (slot == config.getInt("active-menu.items.close.slot", 53)) {
            player.closeInventory();
        } else if (slot == config.getInt("active-menu.items.back.slot", 49)) {
            plugin.getGuiService().openBoosterMenu(player, 0);
        } else if (slot >= 0 && slot < 45) {
            int index = (page * 45) + slot;
            if (index < activeBoosters.size()) {
                ActiveBooster ab = activeBoosters.get(index);
                sendBoosterDetails(player, ab);
                player.closeInventory();
            }
        }
    }

    private void sendBoosterDetails(Player player, ActiveBooster ab) {
        Booster booster = plugin.getBoosterRegistry().getBooster(ab.getBoosterId());
        String displayName = booster != null ? booster.getDisplayName() : ab.getBoosterId();
        double multiplier = booster != null ? booster.getMultiplier() : 1.0;
        long remainingSec = (ab.getExpiresAt() - System.currentTimeMillis()) / 1000;
        if (remainingSec < 0) remainingSec = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startedStr = sdf.format(new Date(ab.getStartedAt()));
        String expiresStr = sdf.format(new Date(ab.getExpiresAt()));

        plugin.getMessageManager().sendRaw(player, "&8&m--------------------------------");
        plugin.getMessageManager().sendRaw(player, "&b&lBooster Details");
        plugin.getMessageManager().sendRaw(player, "&8&m--------------------------------");
        plugin.getMessageManager().sendRaw(player, "&7Name: &f" + displayName);
        plugin.getMessageManager().sendRaw(player, "&7ID: &f" + ab.getBoosterId());
        plugin.getMessageManager().sendRaw(player, "&7Type: &f" + ab.getScope().name());
        if (ab.getOwnerUuid() != null) {
            plugin.getMessageManager().sendRaw(player, "&7Owner: &f" + player.getName());
        } else {
            plugin.getMessageManager().sendRaw(player, "&7Owner: &bServer (Global)");
        }
        plugin.getMessageManager().sendRaw(player, "&7Effect: &a" + multiplier + "x");
        plugin.getMessageManager().sendRaw(player, "&7Started: &f" + startedStr);
        plugin.getMessageManager().sendRaw(player, "&7Remaining: &e" + TextUtil.formatTime(remainingSec));
        plugin.getMessageManager().sendRaw(player, "&7Expires: &f" + expiresStr);
        plugin.getMessageManager().sendRaw(player, "&8&m--------------------------------");
    }
}
