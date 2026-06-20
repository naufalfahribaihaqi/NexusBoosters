package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.Booster;
import id.naufal.nexusboosters.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActivateCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public ActivateCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() { return "activate"; }
    @Override
    public String getDescription() { return "Activate a booster"; }
    @Override
    public String getSyntax() { return "/nb activate <booster>"; }
    @Override
    public String[] getAliases() { return new String[]{"use"}; }
    
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.use");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().sendMessage(sender, "not-player");
            return;
        }
        if (args.length < 1) {
            sender.sendMessage("Usage: " + getSyntax());
            return;
        }

        String boosterId = args[0];
        Booster booster = plugin.getBoosterRegistry().getBooster(boosterId);
        if (booster == null) {
            plugin.getMessageManager().sendMessage(sender, "invalid-booster", "booster", boosterId);
            return;
        }

        plugin.getBoosterManager().tryActivateBooster(player, boosterId, plugin);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getBoosterRegistry().getAllBoosters().stream()
                    .map(Booster::getId)
                    .filter(id -> id.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
