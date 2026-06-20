package id.naufal.nexusboosters.gui;

import org.bukkit.inventory.InventoryHolder;

public interface NexusMenu extends InventoryHolder {
    void onClick(org.bukkit.event.inventory.InventoryClickEvent event);
}
