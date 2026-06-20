package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public HelpCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() { return "help"; }
    @Override
    public String getDescription() { return "Show help menu"; }
    @Override
    public String getSyntax() { return "/nb help"; }
    @Override
    public String[] getAliases() { return new String[]{"?"}; }
    
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.use");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        executeWithLabel(sender, "nb", args);
    }

    @Override
    public void executeWithLabel(CommandSender sender, String label, String[] args) {
        plugin.getMessageManager().sendRaw(sender, "&8&m--------------------------------");
        plugin.getMessageManager().sendRaw(sender, "&b&lNexusBoosters Help");
        plugin.getMessageManager().sendRaw(sender, "&7Boosters command list");
        plugin.getMessageManager().sendRaw(sender, "&8&m--------------------------------");
        
        plugin.getMessageManager().sendRaw(sender, "&a/" + label + " open &8- &7Open your boosters menu.");
        plugin.getMessageManager().sendRaw(sender, "&a/" + label + " active &8- &7View active global and personal boosters.");
        plugin.getMessageManager().sendRaw(sender, "&a/" + label + " activate <booster> &8- &7Activate one of your owned boosters.");
        
        if (sender.hasPermission("nexusboosters.give")) {
            plugin.getMessageManager().sendRaw(sender, "&a/" + label + " give <player> <booster> <amount> [PERSONAL|GLOBAL] [duration] &8- &7Give booster balance to a player.");
        }
        if (sender.hasPermission("nexusboosters.admin")) {
            plugin.getMessageManager().sendRaw(sender, "&a/" + label + " startglobal <booster> [duration] &8- &7Start a global booster with optional custom duration.");
            plugin.getMessageManager().sendRaw(sender, "&a/" + label + " stopglobal <booster> &8- &7Stop a specific active global booster.");
            plugin.getMessageManager().sendRaw(sender, "&a/" + label + " admin &8- &7Open the admin menu.");
            plugin.getMessageManager().sendRaw(sender, "&a/" + label + " sync &8- &7Force cross-server synchronization.");
        }
        if (sender.hasPermission("nexusboosters.reload")) {
            plugin.getMessageManager().sendRaw(sender, "&a/" + label + " reload &8- &7Reload plugin configuration.");
        }
        
        plugin.getMessageManager().sendRaw(sender, "&8&m--------------------------------");
        plugin.getMessageManager().sendRaw(sender, "&7Tip: Use &f/" + label + " open &7to manage your boosters.");
        plugin.getMessageManager().sendRaw(sender, "&8&m--------------------------------");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
