package id.naufal.nexusboosters.listener;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerSessionListener implements Listener {
    private final NexusBoostersPlugin plugin;

    public PlayerSessionListener(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getStorageService().loadPlayerData(event.getPlayer().getUniqueId())
            .thenAccept(data -> plugin.getPlayerManager().loadPlayerData(data));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        id.naufal.nexusboosters.player.PlayerData data = plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
        plugin.getStorageService().savePlayerData(data).thenRun(() -> {
            plugin.getPlayerManager().unloadPlayerData(event.getPlayer().getUniqueId());
        });
    }
}
