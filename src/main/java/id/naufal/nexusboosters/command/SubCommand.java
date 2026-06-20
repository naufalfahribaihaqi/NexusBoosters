package id.naufal.nexusboosters.command;

import org.bukkit.command.CommandSender;
import java.util.List;

public interface SubCommand {
    String getName();
    String getDescription();
    String getSyntax();
    String[] getAliases();
    boolean hasPermission(CommandSender sender);
    void execute(CommandSender sender, String[] args);
    
    default void executeWithLabel(CommandSender sender, String label, String[] args) {
        execute(sender, args);
    }
    
    List<String> tabComplete(CommandSender sender, String[] args);
}
