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
    public String getSyntax() { return "/nb give <player> <booster> <amount> [multiplier] [PERSONAL|GLOBAL] [duration]"; }
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

        String originalBoosterArg = args[1];
        String boosterId = originalBoosterArg;
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

        id.naufal.nexusboosters.booster.BoosterScope scopeOverride = booster.getScope();
        int durationOverride = -1;
        double multiplierOverride = -1.0;

        if (id.naufal.nexusboosters.booster.LegacyIdMapper.isLegacyId(originalBoosterArg)) {
            id.naufal.nexusboosters.booster.LegacyIdMapper.LegacyMapping mapping = id.naufal.nexusboosters.booster.LegacyIdMapper.getMapping(originalBoosterArg);
            boosterId = mapping.getNewId();
            scopeOverride = mapping.getOriginalScope();
            multiplierOverride = mapping.getOriginalMultiplier();
            plugin.getLogger().info("Mapped legacy command alias " + originalBoosterArg + " to " + boosterId);
        }

        for (int i = 3; i < args.length; i++) {
            String arg = args[i];

            // Try to parse as scope
            try {
                scopeOverride = id.naufal.nexusboosters.booster.BoosterScope.valueOf(arg.toUpperCase());
                continue;
            } catch (IllegalArgumentException ignored) {}

            // Try to parse as duration
            int dur = id.naufal.nexusboosters.util.TextUtil.parseDuration(arg);
            if (dur > 0) {
                durationOverride = dur;
                continue;
            }

            // Try to parse as multiplier
            try {
                double mult = Double.parseDouble(arg);
                if (mult > 0 && Double.isFinite(mult)) {
                    multiplierOverride = mult;
                    continue;
                } else {
                    plugin.getMessageManager().sendMessage(sender, "invalid-multiplier");
                    return;
                }
            } catch (NumberFormatException ignored) {}

            plugin.getMessageManager().sendRaw(sender, "&cInvalid argument: " + arg);
            return;
        }

        id.naufal.nexusboosters.player.PlayerData data = plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        id.naufal.nexusboosters.player.PlayerBoosterToken token = new id.naufal.nexusboosters.player.PlayerBoosterToken(boosterId, scopeOverride, durationOverride, multiplierOverride);
        data.addBooster(token, amount);
        plugin.getStorageService().savePlayerData(data);

        String durationStr = durationOverride > 0 ? id.naufal.nexusboosters.util.TextUtil.formatTime(durationOverride) : id.naufal.nexusboosters.util.TextUtil.formatTime(booster.getDurationSeconds());
        String multStr = multiplierOverride > 0 ? String.valueOf(multiplierOverride) : String.valueOf(booster.getMultiplier());

        plugin.getMessageManager().sendMessage(sender, "give-success",
                "amount", String.valueOf(amount),
                "booster_name", booster.getDisplayName(),
                "player", target.getName(),
                "type", scopeOverride.name(),
                "duration", durationStr,
                "multiplier", multStr);
        
        plugin.getMessageManager().sendMessage(target, "receive-booster",
                "amount", String.valueOf(amount),
                "booster_name", booster.getDisplayName(),
                "type", scopeOverride.name(),
                "duration", durationStr,
                "multiplier", multStr);
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
        } else if (args.length >= 4) {
            List<String> suggestions = new ArrayList<>(java.util.Arrays.asList("PERSONAL", "GLOBAL", "1.5", "2.0", "3.0", "30m", "1h", "2h", "1d"));
            return suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
