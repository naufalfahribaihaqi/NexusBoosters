package id.naufal.nexusboosters.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder nameComponent(net.kyori.adventure.text.Component component) {
        if (meta != null && component != null) {
            meta.displayName(component);
        }
        return this;
    }

    public ItemBuilder lore(List<String> loreLines) {
        if (meta != null && loreLines != null) {
            List<net.kyori.adventure.text.Component> componentLore = new ArrayList<>();
            for (String line : loreLines) {
                componentLore.add(TextUtil.color(line));
            }
            meta.lore(componentLore);
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
