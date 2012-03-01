package net.erbros.PvPTime;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldLoadListener implements Listener {
    private PvPTime plugin;
        
    WorldLoadListener(PvPTime instance) {
        plugin = instance;
    }
    @EventHandler()
    public void onWorldInit( WorldInitEvent event )
    {
        World world = event.getWorld();
        plugin.loadWorldConfig(world);
        
    }
    
    
    
}
