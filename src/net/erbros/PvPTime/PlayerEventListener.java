package net.erbros.PvPTime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerEventListener implements Listener {
    private PvPTime plugin;
    
    
    PlayerEventListener(PvPTime instance) {
    	plugin = instance;
    }
    
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event) {
		plugin.checkTimeClock(5);
	}
}