package id.naufal.nexusboosters.hook;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.BoosterType;
import net.brcdev.shopgui.event.ShopGUIPlusPostEnableEvent;
import net.brcdev.shopgui.event.ShopPreTransactionEvent;
import net.brcdev.shopgui.shop.item.ShopItem;
import net.brcdev.shopgui.shop.ShopManager.ShopAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class ShopGUIPlusHook implements Listener {
    private final NexusBoostersPlugin plugin;
    private boolean available = false;

    public ShopGUIPlusHook(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("ShopGUIPlus")) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            plugin.getLogger().info("ShopGUIPlus found, waiting for post-enable event...");
        } else {
            plugin.getLogger().info("ShopGUIPlus not found, hook disabled.");
        }
    }

    public boolean isAvailable() {
        return available;
    }

    @EventHandler
    public void onShopGUIPlusEnable(ShopGUIPlusPostEnableEvent event) {
        this.available = true;
        plugin.getLogger().info("ShopGUIPlus hook enabled.");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPreTransaction(ShopPreTransactionEvent event) {
        if (!available || !plugin.getConfig().getBoolean("shopgui.enabled", true)) return;

        Player player = event.getPlayer();
        ShopItem shopItem = event.getShopItem();
        String shopId = shopItem.getShop().getId();
        
        // Allowed/Blocked shops config
        if (!plugin.getConfig().getBoolean("shopgui.apply-to-all-shops", false)) {
            List<String> allowed = plugin.getConfig().getStringList("shopgui.allowed-shops");
            if (!allowed.isEmpty() && !allowed.contains(shopId)) return;
        }
        List<String> blocked = plugin.getConfig().getStringList("shopgui.blocked-shops");
        if (blocked.contains(shopId)) return;

        ShopAction type = event.getShopAction();
        double originalPrice = event.getPrice();
        double newPrice = originalPrice;

        if (type == ShopAction.BUY) {
            double discountMultiplier = plugin.getBoosterManager().getActiveMultiplier(player.getUniqueId(), BoosterType.SHOP_BUY_DISCOUNT);
            if (discountMultiplier < 1.0) { // e.g. 0.85 means 15% discount
                double maxDiscount = plugin.getConfig().getDouble("shopgui.buy-discount-cap-percent", 30) / 100.0;
                double currentDiscount = 1.0 - discountMultiplier;
                if (currentDiscount > maxDiscount) currentDiscount = maxDiscount;
                newPrice = originalPrice * (1.0 - currentDiscount);
            }
        } else if (type == ShopAction.SELL || type == ShopAction.SELL_ALL) {
            double sellMultiplier = plugin.getBoosterManager().getActiveMultiplier(player.getUniqueId(), BoosterType.SHOP_SELL);
            
            // Apply category specific boost if applicable
            double categoryMultiplier = plugin.getBoosterManager().getActiveMultiplier(player.getUniqueId(), BoosterType.SHOP_CATEGORY_SELL);
            
            double totalMultiplier = sellMultiplier + categoryMultiplier - 1.0;
            
            if (totalMultiplier > 1.0) {
                double maxMultiplier = plugin.getConfig().getDouble("shopgui.sell-boost-cap-multiplier", 3.0);
                if (totalMultiplier > maxMultiplier) totalMultiplier = maxMultiplier;
                newPrice = originalPrice * totalMultiplier;
            }
        }

        if (newPrice < 0) newPrice = 0; // Prevent negative
        event.setPrice(newPrice);
    }
}
