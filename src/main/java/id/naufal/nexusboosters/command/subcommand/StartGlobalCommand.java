package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.Booster;
import id.naufal.nexusboosters.booster.BoosterScope;
import id.naufal.nexusboosters.command.SubCommand;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * /nb startglobal <booster>
 * Admin/console command to directly start a GLOBAL booster for the whole server.
 */
public class StartGlobalCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public StartGlobalCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() { return "startglobal"; }
    @Override
    public String getDescription() { return "Start a global booster for all players"; }
    @Override
    public String getSyntax() { return "/nb startglobal <booster> [duration]"; }
    @Override
    public String[] getAliases() { return new String[]{"sg"}; }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: " + getSyntax());
            return;
        }

        String originalBoosterArg = args[0];
        String boosterId = originalBoosterArg;
        Booster booster = plugin.getBoosterRegistry().getBooster(boosterId);
        if (booster == null) {
            plugin.getMessageManager().sendMessage(sender, "invalid-booster", "booster", boosterId);
            return;
        }

        double multiplierOverride = -1.0;
        if (id.naufal.nexusboosters.booster.LegacyIdMapper.isLegacyId(originalBoosterArg)) {
            id.naufal.nexusboosters.booster.LegacyIdMapper.LegacyMapping mapping = id.naufal.nexusboosters.booster.LegacyIdMapper.getMapping(originalBoosterArg);
            boosterId = mapping.getNewId();
            multiplierOverride = mapping.getOriginalMultiplier();
            plugin.getLogger().info("Mapped legacy command alias " + originalBoosterArg + " to " + boosterId);
        }

        if (booster.getScope() != BoosterScope.GLOBAL) {
            plugin.getMessageManager().sendMessage(sender, "booster-not-global", "booster", booster.getDisplayName());
            return;
        }

        int durationSeconds = booster.getDurationSeconds();
        if (args.length >= 2) {
            int customDuration = TextUtil.parseDuration(args[1]);
            if (customDuration > 0) {
                durationSeconds = customDuration;
            } else {
                plugin.getMessageManager().sendMessage(sender, "invalid-duration");
                return;
            }
        }

        long now = System.currentTimeMillis();
        long expiresAt = now + (durationSeconds * 1000L);
        ActiveBooster activeBooster = new ActiveBooster(boosterId, null, BoosterScope.GLOBAL, multiplierOverride, now, expiresAt);
        plugin.getBoosterManager().activateBooster(activeBooster);
        plugin.getStorageService().saveActiveBooster(activeBooster);

        if (plugin.getBossBarManager() != null) {
            plugin.getBossBarManager().updateOrAddBossBar(activeBooster);
        }

        String durationStr = TextUtil.formatTime(durationSeconds);
        plugin.getMessageManager().sendMessage(sender, "startglobal-success",
                "booster_name", booster.getDisplayName(),
                "duration", durationStr);

        // Broadcast to all players
        for (Player p : Bukkit.getOnlinePlayers()) {
            plugin.getMessageManager().sendMessage(p, "startglobal-success",
                    "booster_name", booster.getDisplayName(),
                    "duration", durationStr);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getBoosterRegistry().getAllBoosters().stream()
                    .filter(b -> b.getScope() == BoosterScope.GLOBAL)
                    .map(Booster::getId)
                    .filter(id -> id.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return java.util.Arrays.asList("30m", "1h", "2h30m", "1d");
        }
        return new ArrayList<>();
    }
}
