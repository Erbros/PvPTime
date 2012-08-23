package net.erbros.PvPTime;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PvPTimeCommandExecutor implements CommandExecutor{
    private PvPTime plugin;
    
    PvPTimeCommandExecutor(PvPTime instance) {
        plugin = instance;
    }
    

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
            String[] args) {
        plugin.debugMsg("PvPTime command:" + label.toString());
        if(args.length > 0) {
            plugin.debugMsg("Args length: " + args.length);
            if(args[0].equalsIgnoreCase("reload")) {
                plugin.debugMsg("Reload command");
                if(sender.hasPermission("pvptime.reload")) {
                    plugin.debugMsg("Calling reloadPvP method.");
                    plugin.reloadPvP();
                    sender.sendMessage(ChatColor.GREEN + "Config reloaded");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have the permission pvptime.reload");
                    return true;
                }
            }
            
        } else {
            plugin.debugMsg("Showing main commands");
            sender.sendMessage("PvPTime plugin");
            sender.sendMessage(ChatColor.GREEN + "/" + label.toString() + " reload" + ChatColor.WHITE + " - Reload configs" );
            return true;
        }
        
        return false;
    }
}
