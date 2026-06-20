package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public ReloadCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() { return "reload"; }
    @Override
    public String getDescription() { return "Reload configuration"; }
    @Override
    public String getSyntax() { return "/nb reload"; }
    @Override
    public String[] getAliases() { return new String[]{"rl"}; }
    
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.getConfigManager().loadAll();
        plugin.getMessageManager().load();
        plugin.getBoosterRegistry().loadFromConfig(plugin.getConfigManager().getBoostersConfig());
        plugin.getMessageManager().sendMessage(sender, "reload-success");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
