package net.erbros.PvPTime;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

public class PlayerEventListener extends PlayerListener {
    private PvPTime plugin;
    
    PlayerEventListener(PvPTime instance) {
    	plugin = instance;
    }
    
    
	public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event) {
		plugin.log.info("Recieved listener call");
		plugin.checkTimeClock(5);
	}
}