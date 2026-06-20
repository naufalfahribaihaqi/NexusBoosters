package id.naufal.nexusboosters;

import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.BoosterManager;
import id.naufal.nexusboosters.booster.BoosterRegistry;
import id.naufal.nexusboosters.command.NexusBoostersCommand;
import id.naufal.nexusboosters.config.ConfigManager;
import id.naufal.nexusboosters.config.MessageManager;
import id.naufal.nexusboosters.database.SQLiteStorageService;
import id.naufal.nexusboosters.database.StorageService;
import id.naufal.nexusboosters.config.GuiConfig;
import id.naufal.nexusboosters.gui.GuiService;
import id.naufal.nexusboosters.hook.HookManager;
import id.naufal.nexusboosters.listener.BlockDropListener;
import id.naufal.nexusboosters.listener.MobDropListener;
import id.naufal.nexusboosters.listener.PlayerSessionListener;
import id.naufal.nexusboosters.listener.XpListener;
import id.naufal.nexusboosters.player.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class NexusBoostersPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private MessageManager messageManager;
    private BoosterRegistry boosterRegistry;
    private BoosterManager boosterManager;
    private StorageService storageService;
    private PlayerManager playerManager;
    private GuiConfig guiConfig;
    private GuiService guiService;
    private HookManager hookManager;
    private id.naufal.nexusboosters.bossbar.BoosterBossBarManager bossBarManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.configManager.loadAll();

        this.messageManager = new MessageManager(this);
        this.messageManager.load();

        this.guiConfig = new GuiConfig(this);
        this.guiConfig.load();

        this.playerManager = new PlayerManager();

        this.boosterRegistry = new BoosterRegistry(getLogger());
        this.boosterRegistry.loadFromConfig(this.configManager.getBoostersConfig());

        this.boosterManager = new BoosterManager(this.boosterRegistry);

        String storageType = getConfig().getString("storage.type", "sqlite").toLowerCase();
        if ("mysql".equals(storageType)) {
            this.storageService = new id.naufal.nexusboosters.database.MySQLStorageService(this);
            getLogger().info("Using MySQL storage.");
        } else {
            this.storageService = new SQLiteStorageService(this);
            getLogger().info("Using SQLite storage.");
        }

        this.storageService.init().thenCompose(v -> this.storageService.loadActiveBoosters())
            .thenAccept(boosters -> {
                if (!this.isEnabled()) return;
                
                this.boosterManager.loadActiveBoosters(boosters);
                this.boosterManager.startExpiryTask(this, this.storageService);
                
                id.naufal.nexusboosters.database.CrossServerSyncTask syncTask = new id.naufal.nexusboosters.database.CrossServerSyncTask(this);
                syncTask.start();
                
                getLogger().info("Successfully loaded " + boosters.size() + " active boosters from database.");
            }).exceptionally(ex -> {
                getLogger().log(Level.SEVERE, "Failed to load active boosters", ex);
                return null;
            });

        // Fix: Register command programmatically instead of JavaPlugin#getCommand()
        getServer().getCommandMap().register("nexusboosters", new id.naufal.nexusboosters.command.NexusBoostersCommandWrapper(new NexusBoostersCommand(this)));

        this.hookManager = new HookManager(this);
        this.hookManager.init();

        this.bossBarManager = new id.naufal.nexusboosters.bossbar.BoosterBossBarManager(this);
        this.bossBarManager.start();

        this.guiService = new GuiService(this);

        org.bukkit.plugin.PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new XpListener(this), this);
        pm.registerEvents(new BlockDropListener(this), this);
        pm.registerEvents(new MobDropListener(this), this);
        pm.registerEvents(new PlayerSessionListener(this), this);

        getLogger().info("NexusBoosters has been enabled!");
        getLogger().info("Version: " + getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {
        // Cancel all async/sync tasks first so expiry task doesn't trigger during shutdown
        getServer().getScheduler().cancelTasks(this);

        if (guiService != null) {
            org.bukkit.event.HandlerList.unregisterAll(guiService);
        }

        if (bossBarManager != null) {
            bossBarManager.stop();
        }

        if (this.storageService != null && this.boosterManager != null) {
            getLogger().info("Saving active boosters to database...");
            List<CompletableFuture<Void>> saveTasks = new ArrayList<>();
            for (ActiveBooster booster : this.boosterManager.getAllActiveBoosters()) {
                saveTasks.add(this.storageService.saveActiveBooster(booster));
            }
            CompletableFuture.allOf(saveTasks.toArray(new CompletableFuture[0])).join();
            
            this.storageService.shutdown().join();
            getLogger().info("Saved " + saveTasks.size() + " active boosters.");
        }
        getLogger().info("NexusBoosters has been disabled!");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public GuiConfig getGuiConfig() { return guiConfig; }
    public BoosterRegistry getBoosterRegistry() { return boosterRegistry; }
    public BoosterManager getBoosterManager() { return boosterManager; }
    public StorageService getStorageService() { return storageService; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public GuiService getGuiService() { return guiService; }
    public HookManager getHookManager() { return hookManager; }
    public id.naufal.nexusboosters.bossbar.BoosterBossBarManager getBossBarManager() { return bossBarManager; }
}
