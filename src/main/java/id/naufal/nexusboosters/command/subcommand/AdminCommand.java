package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public AdminCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() { return "admin"; }
    @Override
    public String getDescription() { return "Open admin menu"; }
    @Override
    public String getSyntax() { return "/nb admin"; }
    @Override
    public String[] getAliases() { return new String[0]; }
    
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(sender, "console-no-gui");
            return;
        }
        Player player = (Player) sender;
        plugin.getGuiService().openAdminMenu(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
