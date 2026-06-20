package id.naufal.nexusboosters.config;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class GuiConfig {
    private final NexusBoostersPlugin plugin;
    private FileConfiguration config;
    private File file;

    public GuiConfig(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        file = new File(plugin.getDataFolder(), "gui.yml");
        if (!file.exists()) {
            plugin.saveResource("gui.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
