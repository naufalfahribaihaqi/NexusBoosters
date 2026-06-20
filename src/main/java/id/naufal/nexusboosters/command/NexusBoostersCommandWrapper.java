package id.naufal.nexusboosters.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class NexusBoostersCommandWrapper extends Command {
    private final NexusBoostersCommand executor;

    public NexusBoostersCommandWrapper(NexusBoostersCommand executor) {
        super("nexusboosters");
        this.executor = executor;
        this.setAliases(java.util.Arrays.asList("nb", "boosters"));
        this.setPermission("nexusboosters.use");
        this.setDescription("Main command for NexusBoosters");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return executor.onCommand(sender, this, commandLabel, args);
    }

    @NotNull
    @Override
    public java.util.List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        java.util.List<String> completions = executor.onTabComplete(sender, this, alias, args);
        return completions == null ? java.util.Collections.emptyList() : completions;
    }
}
