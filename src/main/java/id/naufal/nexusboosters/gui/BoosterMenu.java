package id.naufal.nexusboosters.gui;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.Booster;
import id.naufal.nexusboosters.booster.BoosterType;
import id.naufal.nexusboosters.player.PlayerData;
import id.naufal.nexusboosters.util.ItemBuilder;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoosterMenu implements NexusMenu {
    private final NexusBoostersPlugin plugin;
    private final Player player;
    private final int page;
    private final Inventory inventory;
    private final List<id.naufal.nexusboosters.player.PlayerBoosterToken> displayBoosters;

    public BoosterMenu(NexusBoostersPlugin plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        
        boolean showUnowned = plugin.getGuiConfig().getConfig().getBoolean("show-unowned-boosters", false);
        PlayerData data = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        
        this.displayBoosters = new ArrayList<>();
        java.util.Set<String> addedOwned = new java.util.HashSet<>();

        for (java.util.Map.Entry<id.naufal.nexusboosters.player.PlayerBoosterToken, Integer> entry : data.getBoosterInventory().entrySet()) {
            if (entry.getValue() > 0) {
                displayBoosters.add(entry.getKey());
                addedOwned.add(entry.getKey().getBoosterId());
            }
        }

        for (Booster b : plugin.getBoosterRegistry().getAllBoosters()) {
            if (showUnowned && !addedOwned.contains(b.getId())) {
                displayBoosters.add(new id.naufal.nexusboosters.player.PlayerBoosterToken(b.getId(), b.getScope(), -1, -1.0));
            }
        }
        
        FileConfiguration config = plugin.getGuiConfig().getConfig();
        String title = config.getString("booster-menu.title", "\u00268Booster Menu");
        int size = config.getInt("booster-menu.size", 54);
        
        this.inventory = Bukkit.createInventory(this, size, TextUtil.color(title));

        setupItems(config);
    }

    private void setupItems(FileConfiguration config) {
        PlayerData data = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        int startIndex = page * 45; 
        int slot = 0;
        
        for (int i = startIndex; i < Math.min(startIndex + 45, displayBoosters.size()); i++) {
            id.naufal.nexusboosters.player.PlayerBoosterToken token = displayBoosters.get(i);
            Booster b = plugin.getBoosterManager().getEffectiveBooster(token);
            if (b == null) continue;

            Material mat = Material.matchMaterial(b.getMaterial());
            if (mat == null) mat = Material.STONE;
            
            int owned = data.getBoosterAmount(token);
            boolean isOwned = owned > 0;
            
            // Check hook requirements silently
            boolean hooksMet = checkHookRequirements(b);
            
            List<String> lore = new ArrayList<>();
            lore.add("&7Effect: &f" + getEffectDescription(b));
            lore.add("&7Type: &f" + (b.getScope().name().equals("GLOBAL") ? "&bGlobal Booster" : "&aPersonal Booster"));
            lore.add("&7Duration: &e" + TextUtil.formatTime(b.getDurationSeconds()));
            
            if (isOwned) {
                lore.add("&7Owned: &a" + owned + "x");
                lore.add("");
                if (!hooksMet) {
                    lore.add("&cCurrently unavailable.");
                } else {
                    lore.add("&aClick to activate!");
                }
            } else {
                lore.add("");
                lore.add("&cYou do not own this booster.");
            }
            
            if (!isOwned) {
                mat = Material.GRAY_DYE;
            } else if (!hooksMet) {
                mat = Material.BARRIER;
            }
            
            ItemStack item = new ItemBuilder(mat)
                    .nameComponent(TextUtil.color(b.getDisplayName()))
                    .lore(lore)
                    .build();
            
            inventory.setItem(slot++, item);
        }

        if (page > 0) {
            inventory.setItem(config.getInt("booster-menu.items.prev-page.slot", 48), 
                    createItem(config, "booster-menu.items.prev-page"));
        }
        if (startIndex + 45 < displayBoosters.size()) {
            inventory.setItem(config.getInt("booster-menu.items.next-page.slot", 50), 
                    createItem(config, "booster-menu.items.next-page"));
        }
        
        inventory.setItem(config.getInt("booster-menu.items.active-boosters.slot", 49), 
                createItem(config, "booster-menu.items.active-boosters"));
                
        inventory.setItem(config.getInt("booster-menu.items.close.slot", 53), 
                createItem(config, "booster-menu.items.close"));
    }

    private String getEffectDescription(Booster b) {
        BoosterType type = b.getType();
        double mult = b.getMultiplier();
        return switch (type) {
            case XP -> mult + "x XP gain";
            case MONEY -> mult + "x money earn";
            case BLOCK_DROPS -> mult + "x block drops";
            case MOB_DROPS -> mult + "x mob drops";
            case PLAYERPOINTS_GAIN -> mult + "x PlayerPoints reward";
            case SHOP_SELL, SHOP_CATEGORY_SELL -> mult + "x shop sell price";
            case SHOP_BUY_DISCOUNT -> {
                int pct = (int) Math.round((1.0 - mult) * 100);
                yield pct + "% shop buy discount";
            }
            case SHOP_POINTS_DISCOUNT -> {
                int pct = (int) Math.round((1.0 - mult) * 100);
                yield pct + "% points cost discount";
            }
            case PLAYERPOINTS_SHOP_DISCOUNT -> {
                int pct = (int) Math.round((1.0 - mult) * 100);
                yield pct + "% points shop discount";
            }
            case PLAYERPOINTS_SELL_BONUS -> mult + "x points sell bonus";
            default -> mult + "x boost";
        };
    }

    private boolean checkHookRequirements(Booster b) {
        for (String hook : b.getRequiresHooks()) {
            if (hook.equalsIgnoreCase("PlayerPoints") && !plugin.getHookManager().getPlayerPointsHook().isAvailable()) return false;
            if (hook.equalsIgnoreCase("ShopGUIPlus") && !plugin.getHookManager().getShopGUIPlusHook().isAvailable()) return false;
        }
        return true;
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
        
        if (slot == config.getInt("booster-menu.items.close.slot", 53)) {
            player.closeInventory();
        } else if (slot == config.getInt("booster-menu.items.active-boosters.slot", 49)) {
            plugin.getGuiService().openActiveBoosterMenu(player, 0);
        } else if (slot == config.getInt("booster-menu.items.prev-page.slot", 48) && page > 0) {
            plugin.getGuiService().openBoosterMenu(player, page - 1);
        } else if (slot == config.getInt("booster-menu.items.next-page.slot", 50) && (page + 1) * 45 < displayBoosters.size()) {
            plugin.getGuiService().openBoosterMenu(player, page + 1);
        } else if (slot >= 0 && slot < 45) {
            int index = (page * 45) + slot;
            if (index < displayBoosters.size()) {
                id.naufal.nexusboosters.player.PlayerBoosterToken token = displayBoosters.get(index);
                Booster b = plugin.getBoosterManager().getEffectiveBooster(token);
                if (b == null) return;
                PlayerData data = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                
                if (data.getBoosterAmount(token) <= 0) {
                    plugin.getMessageManager().sendMessage(player, "booster-no-balance");
                    return;
                }
                
                if (!checkHookRequirements(b)) {
                    plugin.getMessageManager().sendMessage(player, "booster-unavailable");
                    return;
                }
                
                plugin.getGuiService().openConfirmMenu(player, token);
            }
        }
    }
}
