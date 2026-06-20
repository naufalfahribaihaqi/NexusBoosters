package id.naufal.nexusboosters.config;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager {
    private final NexusBoostersPlugin plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private String prefix;

    public MessageManager(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        prefix = messagesConfig.getString("prefix", "&8[&bNexusBoosters&8] &7");
    }

    public void sendMessage(CommandSender sender, String key) {
        String msg = messagesConfig.getString(key);
        if (msg == null || msg.isEmpty()) return;
        sender.sendMessage(TextUtil.color(prefix + msg));
    }

    public void sendMessage(CommandSender sender, String key, String... placeholders) {
        String msg = messagesConfig.getString(key);
        if (msg == null || msg.isEmpty()) return;
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                msg = msg.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        sender.sendMessage(TextUtil.color(prefix + msg));
    }

    public void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(TextUtil.color(message));
    }
}
