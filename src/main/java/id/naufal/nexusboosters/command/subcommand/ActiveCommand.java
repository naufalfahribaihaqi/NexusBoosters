package id.naufal.nexusboosters.command.subcommand;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.Booster;
import id.naufal.nexusboosters.command.SubCommand;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ActiveCommand implements SubCommand {
    private final NexusBoostersPlugin plugin;

    public ActiveCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() { return "active"; }
    @Override
    public String getDescription() { return "View active boosters"; }
    @Override
    public String getSyntax() { return "/nb active"; }
    @Override
    public String[] getAliases() { return new String[]{"list"}; }
    
    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("nexusboosters.use");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Console-safe: does not open GUI
        plugin.getMessageManager().sendRaw(sender, "&8&m--------------------------------");
        plugin.getMessageManager().sendRaw(sender, "&b&lActive Boosters");
        plugin.getMessageManager().sendRaw(sender, "&8&m--------------------------------");

        List<ActiveBooster> activeBoosters = plugin.getBoosterManager().getAllActiveBoosters();
        boolean found = false;

        for (ActiveBooster active : activeBoosters) {
            if (!active.isExpired()) {
                boolean show = active.getOwnerUuid() == null;
                if (!show && sender instanceof Player p && active.getOwnerUuid().equals(p.getUniqueId())) {
                    show = true;
                }
                if (!show && !(sender instanceof Player)) {
                    show = true; // Console sees all
                }

                if (show) {
                    Booster booster = plugin.getBoosterRegistry().getBooster(active.getBoosterId());
                    String displayName = booster != null ? booster.getDisplayName() : active.getBoosterId();
                    long remainingSec = (active.getExpiresAt() - System.currentTimeMillis()) / 1000;
                    if (remainingSec < 0) remainingSec = 0;
                    
                    plugin.getMessageManager().sendRaw(sender, "&7- &b" + displayName + " &8(&7" + active.getScope().name() + "&8): &a" + TextUtil.formatTime(remainingSec) + " remaining");
                    found = true;
                }
            }
        }

        if (!found) {
            plugin.getMessageManager().sendRaw(sender, "&cNo active boosters.");
        }
        plugin.getMessageManager().sendRaw(sender, "&8&m--------------------------------");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
