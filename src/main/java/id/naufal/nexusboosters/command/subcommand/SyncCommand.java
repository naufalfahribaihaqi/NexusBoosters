package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SyncCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public SyncCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "sync";
    }

    @Override
    public String getDescription() {
        return "Force cross-server synchronization";
    }

    @Override
    public String getSyntax() {
        return "/nb sync";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!plugin.getConfig().getBoolean("cross-server.enabled", false)) {
            plugin.getMessageManager().sendMessage(sender, "sync-disabled");
            return;
        }

        plugin.getMessageManager().sendMessage(sender, "sync-started");
        plugin.getStorageService().loadActiveBoosters().thenAccept(dbBoosters -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getBoosterManager().syncFromDatabase(dbBoosters);
                plugin.getMessageManager().sendMessage(sender, "sync-complete");
            });
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
