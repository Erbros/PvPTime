package net.erbros.PvPTime;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.util.config.Configuration;

public class WorldLoadListener extends WorldListener {
    private PvPTime plugin;
        
    WorldLoadListener(PvPTime instance) {
        plugin = instance;
    }
    
    public void onWorldLoad( WorldLoadEvent event )
    {
        
        Configuration config = plugin.getConfiguration();
        config.load();
        World w = event.getWorld();
        
        // First, making a HashMap to go inside the global HashMap of the worlds.
        HashMap<String,Object> currentWorld = new HashMap<String, Object>();
        // before we put this HashMap inside the global hashmap, let's fill it up.
        currentWorld.put("enabled", config.getBoolean("world." + w.getName() + ".enabled", false));
        currentWorld.put("startTime", config.getInt("world." + w.getName() + ".start.time", 13000));
        currentWorld.put("startMsg", config.getString("world." + w.getName() + ".start.msg.text", "It's night and PvP is turned on"));
        currentWorld.put("startMsgColor", config.getString("world." + w.getName() + ".start.msg.color", "DARK_RED"));
        currentWorld.put("startMsgBroadcast", config.getBoolean("world." + w.getName() + ".start.msg.broadcast", true));
        currentWorld.put("endTime", config.getInt("world." + w.getName() + ".end.time", 1000));
        currentWorld.put("endMsg", config.getString("world." + w.getName() + ".end.msg.text", "It's daytime and PvP is turned off"));
        currentWorld.put("endMsgColor", config.getString("world." + w.getName() + ".end.msg.color", "GREEN"));
        currentWorld.put("endMsgBroadcast", config.getBoolean("world." + w.getName() + ".end.msg.broadcast", true));
        currentWorld.put("forcePvP", config.getBoolean("world." + w.getName() + ".forcePvP", false));
        currentWorld.put("overrideEnabled", config.getBoolean("world." + w.getName() + ".override", false));
        
        
        // Let's put the currentWorld in pvpWorlds hashMap
        plugin.pvpWorlds.put(w.getName(), currentWorld);
        // lets remove currentWorld, just in case.
        config.save();
        // Let's run the plugin refresh so it knows we have some worlds for it ;)
        plugin.reloadPvP();
        
    }
    
}
