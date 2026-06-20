package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.BoosterScope;
import id.naufal.nexusboosters.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StopGlobalCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public StopGlobalCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "stopglobal";
    }

    @Override
    public String getDescription() {
        return "Stop active global booster.";
    }

    @Override
    public String getSyntax() {
        return "/nb stopglobal <booster>";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.stopglobal") || sender.hasPermission("nexusboosters.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: " + getSyntax());
            return;
        }

        String target = args[0];
        List<ActiveBooster> activeGlobals = plugin.getBoosterManager().getAllActiveBoosters().stream()
                .filter(b -> b.getScope() == BoosterScope.GLOBAL)
                .collect(Collectors.toList());

        if (activeGlobals.isEmpty()) {
            plugin.getMessageManager().sendMessage(sender, "no-active-global-booster", "booster", target);
            return;
        }

        if (target.equalsIgnoreCase("all")) {
            plugin.getMessageManager().sendMessage(sender, "stopglobal-all-disabled");
            return;
        }

        boolean found = false;
        for (ActiveBooster booster : activeGlobals) {
            if (booster.getBoosterId().equalsIgnoreCase(target)) {
                plugin.getStorageService().removeActiveBooster(booster.getBoosterId(), null);
                if (plugin.getBossBarManager() != null) {
                    plugin.getBossBarManager().removeBossBar(booster.getBoosterId());
                }
                found = true;
            }
        }

        if (found) {
            plugin.getStorageService().loadActiveBoosters().thenAccept(dbBoosters -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.getBoosterManager().syncFromDatabase(dbBoosters);
                });
            });
            plugin.getMessageManager().sendMessage(sender, "global-booster-stopped", "booster_name", target);
        } else {
            plugin.getMessageManager().sendMessage(sender, "no-active-global-booster", "booster", target);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> activeIds = plugin.getBoosterManager().getAllActiveBoosters().stream()
                    .filter(b -> b.getScope() == BoosterScope.GLOBAL)
                    .map(ActiveBooster::getBoosterId)
                    .collect(Collectors.toList());
            return activeIds.stream()
                    .filter(id -> id.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
