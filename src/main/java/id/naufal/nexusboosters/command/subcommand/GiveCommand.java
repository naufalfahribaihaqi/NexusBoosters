package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.Booster;
import id.naufal.nexusboosters.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public GiveCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() { return "give"; }
    @Override
    public String getDescription() { return "Give booster balance to a player"; }
    @Override
    public String getSyntax() { return "/nb give <player> <booster> <amount> [PERSONAL|GLOBAL] [duration]"; }
    @Override
    public String[] getAliases() { return new String[]{"add"}; }
    
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.give");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: " + getSyntax());
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageManager().sendMessage(sender, "player-not-found", "player", args[0]);
            return;
        }

        String boosterId = args[1];
        Booster booster = plugin.getBoosterRegistry().getBooster(boosterId);
        if (booster == null) {
            plugin.getMessageManager().sendMessage(sender, "invalid-booster", "booster", boosterId);
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                plugin.getMessageManager().sendMessage(sender, "invalid-amount");
                return;
            }
        } catch (NumberFormatException e) {
            plugin.getMessageManager().sendMessage(sender, "invalid-amount");
            return;
        }

        id.naufal.nexusboosters.booster.BoosterScope scope = booster.getScope();
        int durationOverride = -1;
        if (args.length >= 4) {
            try {
                scope = id.naufal.nexusboosters.booster.BoosterScope.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                durationOverride = id.naufal.nexusboosters.util.TextUtil.parseDuration(args[3]);
                if (durationOverride <= 0) {
                    plugin.getMessageManager().sendMessage(sender, "invalid-type");
                    return;
                }
            }
        }

        if (args.length >= 5 && durationOverride == -1) {
            durationOverride = id.naufal.nexusboosters.util.TextUtil.parseDuration(args[4]);
            if (durationOverride <= 0) {
                plugin.getMessageManager().sendMessage(sender, "invalid-duration");
                return;
            }
        }

        id.naufal.nexusboosters.player.PlayerData data = plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        id.naufal.nexusboosters.player.PlayerBoosterToken token = new id.naufal.nexusboosters.player.PlayerBoosterToken(boosterId, scope, durationOverride);
        data.addBooster(token, amount);
        plugin.getStorageService().savePlayerData(data);

        String durationStr = durationOverride > 0 ? id.naufal.nexusboosters.util.TextUtil.formatTime(durationOverride) : id.naufal.nexusboosters.util.TextUtil.formatTime(booster.getDurationSeconds());

        plugin.getMessageManager().sendMessage(sender, "give-success",
                "amount", String.valueOf(amount),
                "booster_name", booster.getDisplayName(),
                "player", target.getName(),
                "type", scope.name(),
                "duration", durationStr);
        
        plugin.getMessageManager().sendMessage(target, "receive-booster",
                "amount", String.valueOf(amount),
                "booster_name", booster.getDisplayName(),
                "type", scope.name(),
                "duration", durationStr);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return plugin.getBoosterRegistry().getAllBoosters().stream()
                    .map(Booster::getId)
                    .filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            return java.util.Arrays.asList("1", "5", "10");
        } else if (args.length == 4) {
            return java.util.Arrays.asList("GLOBAL", "PERSONAL").stream()
                    .filter(type -> type.startsWith(args[3].toUpperCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 5) {
            return java.util.Arrays.asList("30m", "1h", "2h", "1d");
        }
        return new ArrayList<>();
    }
}
