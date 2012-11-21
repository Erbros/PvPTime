package net.erbros.PvPTime;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.ChatColor;


public class PvPTime extends JavaPlugin {

	public boolean pvpPluginDisable;
	
	private DamageListener dL = new DamageListener(this);
	private PlayerEventListener pEL = new PlayerEventListener(this);
	private WorldLoadListener wLL = new WorldLoadListener(this);
	public HashMap<String,HashMap<String,Object>> pvpWorlds = new HashMap<String,HashMap<String,Object>>();
	public HashMap<String,Boolean> pvpAnnouncedWorlds = new HashMap<String, Boolean>();
    protected PvPTimeCommandExecutor myExecutor;
	// Getting some logging done.
	protected final Logger log = Logger.getLogger("Minecraft");
	public boolean debug;
	
	
	@Override
	public void onDisable() {
		// Disable all running timers.
		Bukkit.getServer().getScheduler().cancelTasks(this);
	}

	@Override
	public void onEnable() {
	    // Do we have debug enabled?
	    debug = getConfig().getBoolean("settings.debug", false);
	    
        getDataFolder().mkdirs();
	    // Any enabled worlds already?
        for(World w : Bukkit.getServer().getWorlds()) {
            loadWorldConfig(w);
        }

        // Get ready for the commands
        myExecutor = new PvPTimeCommandExecutor(this);
        getCommand("pvptime").setExecutor(myExecutor);
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(dL, this);
		pm.registerEvents(pEL, this);
		pm.registerEvents(wLL, this);
		
	}
	
	public void reloadPvP() {
        debug = getConfig().getBoolean("settings.debug", false);
        debugMsg("Reloading config");
        boolean anyPvPBroadcast = false;
	    for(World w : Bukkit.getServer().getWorlds()){
            if((Boolean) getValue(pvpWorlds,w.getName(),"enabled") == true) {
                
                
                // Checking if we start with pvpTime
                if(dL.isItPvPTime(w.getName())) {
                    pvpAnnouncedWorlds.put(w.getName(), true);
                } else {
                    pvpAnnouncedWorlds.put(w.getName(), false);
                }
                

                // Are we forcing the pvp setting on?
                if((Boolean) getValue(pvpWorlds, w.getName() ,"forcePvP")) {
                    getServer().getWorld(w.getName()).setPVP(true);
                    debugMsg("Forced pvp on in world: " + w.getName());
                }
                // Any broadcast? Do we need a timer?
                if((Boolean) getValue(pvpWorlds, w.getName() ,"startMsgBroadcast") || (Boolean) getValue(pvpWorlds, w.getName() ,"endMsgBroadcast")) {
                    anyPvPBroadcast = true;
                    debugMsg("We need to check the time.");
                }
            }
        }
	    
	    if(anyPvPBroadcast) {
            checkTime();
        }
	}
	
	public void checkTime() {
		// is it pvp on now?
        long lowestTimeLeft = 0;
        debugMsg("Checking time");
	    
	    for(World w : Bukkit.getServer().getWorlds()){
    	    if((Boolean) getValue(pvpWorlds,w.getName(),"enabled")) {
    	        debugMsg(w.getName() + " enabled. Checking.");
    	        if((Boolean) getValue(pvpWorlds,w.getName(),"startMsgBroadcast") || (Boolean) getValue(pvpWorlds,w.getName(),"endMsgBroadcast")) {
            		if(dL.isItPvPTime(w.getName())) {
            			// it's pvp time, but have we announced it?
            			if(pvpAnnouncedWorlds.get(w.getName()) == false) {
            				announceNow(true,w.getName());
            				debugMsg("Announcing pvp time");
            			}
            			
            		} else {
            			// it's not pvp time, but have we announced it?
            			if(pvpAnnouncedWorlds.get(w.getName()) == true) {
            				announceNow(false,w.getName());
            				debugMsg("Announcing end of pvp time");
            			}
            			
            		}
            		// Is this the lowest time left?
            		if(nextBroadcast(w.getName()) < lowestTimeLeft || lowestTimeLeft == 0) {
            		    lowestTimeLeft = nextBroadcast(w.getName());
                        debugMsg("New lowest time: " + lowestTimeLeft);
            		}
    	        }
    	    }
	    }
		checkTimeClock(Math.round(lowestTimeLeft/3+1));
		
	}
	
	// Check time for next broadcast in world
	public Long nextBroadcast(String world) {
	    long nextBroadcast = 0;
	    
	    if(dL.isItPvPTime(world)) {
            // Check how long until pvp is over.
            if(Bukkit.getWorld(world).getTime() > Long.parseLong(getValue(pvpWorlds,world,"endTime").toString())) {
                nextBroadcast = 24000 - Bukkit.getWorld(world).getTime() + Long.parseLong(getValue(pvpWorlds,world,"endTime").toString());
            } else {
                nextBroadcast = Long.parseLong(getValue(pvpWorlds,world,"endTime").toString()) - Bukkit.getWorld(world).getTime();
            }
        } else {
            if(Bukkit.getWorld(world).getTime() > Long.parseLong(getValue(pvpWorlds,world,"startTime").toString())) {
                nextBroadcast = 24000 - Bukkit.getWorld(world).getTime() + Long.parseLong(getValue(pvpWorlds,world,"startTime").toString());
            } else {
                nextBroadcast = Long.parseLong(getValue(pvpWorlds,world,"startTime").toString()) - Bukkit.getWorld(world).getTime();
            }
        }
	    
	    return nextBroadcast;
	}
	
	public void checkTimeClock(long countdown) {
		getServer().getScheduler().cancelTasks(this);
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
				@Override
                public void run() {
					checkTime();
				}
			}, countdown);
	}
	
	public void announceNow (boolean pvpOn, String world) {
		if(pvpOn) {
			if((Boolean) getValue(pvpWorlds,world,"startMsgBroadcast")) {
			    broadcastAllPlayersWorld(Bukkit.getWorld(world), (String) getValue(pvpWorlds,world,"startMsg"));
			}
			pvpAnnouncedWorlds.put(world, true);			
		} else {
			if((Boolean) getValue(pvpWorlds,world,"endMsgBroadcast")) {
			    broadcastAllPlayersWorld(Bukkit.getWorld(world), (String) getValue(pvpWorlds,world,"endMsg"));
			}
			pvpAnnouncedWorlds.put(world, false); 
		}
	}
	
	// Get value from hashmap inside hashmap
	public Object getValue (HashMap<String,HashMap<String,Object>> map, String mainKey, String nodeKey) {
	    // do mainKey exist?
	    if(!map.containsKey(mainKey)) {
	        return false;
	    }
        HashMap<String,Object> obj = map.get(mainKey);
        if(!obj.containsKey(nodeKey)) {
            return false;
        }
        Object value = obj.get(nodeKey);
        
        // Null check
        if(value == null) {
            value = false;
        }
        
	    return value;
	}
	
	public void broadcastAllPlayersWorld(World world, String message) {
	    List<Player> players = world.getPlayers();
	    for(Player p : players) {
	        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	    }
	}
	
	public void loadWorldConfig(World w) {
        Configuration config = getConfig();
        
        if(!config.contains("world." + w.getName() + ".enabled")) {
            setWorldConfig(w);
        }
        
        // First, making a HashMap to go inside the global HashMap of the worlds.
        HashMap<String,Object> currentWorld = new HashMap<String, Object>();
        // before we put this HashMap inside the global hashmap, let's fill it up.
        currentWorld.put("enabled", config.getBoolean("world." + w.getName() + ".enabled", false));
        currentWorld.put("startTime", config.getInt("world." + w.getName() + ".start.time", 13000));
        currentWorld.put("startMsg", config.getString("world." + w.getName() + ".start.msg.text", "&4It's night and PvP is turned on"));
        currentWorld.put("startMsgBroadcast", config.getBoolean("world." + w.getName() + ".start.msg.broadcast", true));
        currentWorld.put("endTime", config.getInt("world." + w.getName() + ".end.time", 1000));
        currentWorld.put("endMsg", config.getString("world." + w.getName() + ".end.msg.text", "&aIt's daytime and PvP is turned off"));
        currentWorld.put("endMsgBroadcast", config.getBoolean("world." + w.getName() + ".end.msg.broadcast", true));
        currentWorld.put("forcePvP", config.getBoolean("world." + w.getName() + ".forcePvP", false));
        currentWorld.put("overrideEnabled", config.getBoolean("world." + w.getName() + ".override", false));
        
        
        // Let's put the currentWorld in pvpWorlds hashMap
        pvpWorlds.put(w.getName(), currentWorld);
        // Let's run the plugin refresh so it knows we have some worlds for it ;)
        reloadPvP();
    }
	
	public void setWorldConfig(World w) {
        Configuration config = getConfig();

        config.set("world." + w.getName() + ".enabled", false);
        config.set("world." + w.getName() + ".start.time", 13000);
        config.set("world." + w.getName() + ".start.msg.text", "&4It's night and PvP is turned on");
        config.set("world." + w.getName() + ".start.msg.broadcast", true);
        config.set("world." + w.getName() + ".end.time", 1000);
        config.set("world." + w.getName() + ".end.msg.text", "&aIt's daytime and PvP is turned off");
        config.set("world." + w.getName() + ".end.msg.broadcast", true);
        config.set("world." + w.getName() + ".forcePvP", false);
        config.set("world." + w.getName() + ".override", false);
        
        saveConfig();
        
	}
	
	//debug msg
	public void debugMsg (String msg) {
	    if(debug)
	        log.info("PvPTime debug: " + msg);
	}

}
