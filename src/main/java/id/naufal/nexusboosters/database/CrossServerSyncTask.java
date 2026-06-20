package id.naufal.nexusboosters.database;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import org.bukkit.scheduler.BukkitTask;

public class CrossServerSyncTask implements Runnable {
    private final NexusBoostersPlugin plugin;
    private BukkitTask task;

    public CrossServerSyncTask(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("cross-server.enabled", false)) return;
        
        String storageType = plugin.getConfig().getString("storage.type", "sqlite").toLowerCase();
        if ("sqlite".equals(storageType)) {
            plugin.getLogger().warning("Cross-server sync is enabled, but storage type is SQLite. Cross-server sync requires MySQL storage to function across multiple servers. It will run locally, but not sync globally.");
        }

        long intervalTicks = plugin.getConfig().getLong("cross-server.sync-interval-seconds", 5) * 20L;
        if (task != null) {
            task.cancel();
        }
        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, intervalTicks, intervalTicks);
        plugin.getLogger().info("Cross-server sync task started.");
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void run() {
        if (plugin.getStorageService() == null || plugin.getBoosterManager() == null) return;
        
        plugin.getStorageService().loadActiveBoosters().thenAccept(dbBoosters -> {
            if (!plugin.isEnabled()) return;
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getBoosterManager().syncFromDatabase(dbBoosters);
                
                // Update bossbars for new sync state
                if (plugin.getBossBarManager() != null) {
                    // BossBarManager should refresh or check if its bossbars match the manager's active boosters
                    // For safety, we can rely on BossBarManager's rotation or update tasks.
                }
            });
        });
    }
}
