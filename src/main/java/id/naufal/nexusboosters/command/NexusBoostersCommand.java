package id.naufal.nexusboosters.command;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.command.subcommand.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NexusBoostersCommand implements CommandExecutor, TabCompleter {
    private final NexusBoostersPlugin plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public NexusBoostersCommand(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
        registerSubCommand(new HelpCommand(plugin));
        registerSubCommand(new OpenCommand(plugin));
        registerSubCommand(new ActiveCommand(plugin));
        registerSubCommand(new ActivateCommand(plugin));
        registerSubCommand(new GiveCommand(plugin));
        registerSubCommand(new StartGlobalCommand(plugin));
        registerSubCommand(new StopGlobalCommand(plugin));
        registerSubCommand(new SyncCommand(plugin));
        registerSubCommand(new ReloadCommand(plugin));
        registerSubCommand(new AdminCommand(plugin));
    }

    private void registerSubCommand(SubCommand cmd) {
        subCommands.put(cmd.getName().toLowerCase(), cmd);
        for (String alias : cmd.getAliases()) {
            subCommands.put(alias.toLowerCase(), cmd);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof org.bukkit.entity.Player)) {
                subCommands.get("help").executeWithLabel(sender, label, args);
            } else {
                SubCommand open = subCommands.get("open");
                if (open.hasPermission(sender)) {
                    open.executeWithLabel(sender, label, args);
                } else {
                    plugin.getMessageManager().sendMessage(sender, "no-permission");
                }
            }
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            subCommands.get("help").executeWithLabel(sender, label, args);
            return true;
        }

        if (!subCommand.hasPermission(sender)) {
            plugin.getMessageManager().sendMessage(sender, "no-permission");
            return true;
        }

        subCommand.executeWithLabel(sender, label, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommands.values().stream()
                    .filter(cmd -> cmd.hasPermission(sender))
                    .map(SubCommand::getName)
                    .distinct()
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null && subCommand.hasPermission(sender)) {
                return subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return new ArrayList<>();
    }
}
