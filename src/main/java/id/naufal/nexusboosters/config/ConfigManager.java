package id.naufal.nexusboosters.config;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {
    private final NexusBoostersPlugin plugin;
    private FileConfiguration boostersConfig;
    private File boostersFile;

    public ConfigManager(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        plugin.saveDefaultConfig();
        
        boostersFile = new File(plugin.getDataFolder(), "boosters.yml");
        if (!boostersFile.exists()) {
            plugin.saveResource("boosters.yml", false);
        }
        boostersConfig = YamlConfiguration.loadConfiguration(boostersFile);
    }

    public FileConfiguration getBoostersConfig() {
        return boostersConfig;
    }
}
