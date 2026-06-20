package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OpenCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public OpenCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() { return "open"; }
    @Override
    public String getDescription() { return "Open the boosters GUI"; }
    @Override
    public String getSyntax() { return "/nb open"; }
    @Override
    public String[] getAliases() { return new String[]{"gui", "menu"}; }
    
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.use");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().sendMessage(sender, "console-no-gui");
            return;
        }
        plugin.getGuiService().openBoosterMenu(player, 0);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
