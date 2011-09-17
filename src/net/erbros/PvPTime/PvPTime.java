package net.erbros.PvPTime;

import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


public class PvPTime extends JavaPlugin {

	
	public int pvpStartTime;
	public String pvpStartMsg;
	public String pvpStartMsgColor;
	public boolean pvpStartMsgBroadcast;
	public int pvpEndTime;
	public String pvpEndMsg;
	public String pvpEndMsgColor;
	public boolean pvpEndMsgBroadcast;
	public String pvpWorldName;
	public boolean pvpAnnouncedPvP;
	public boolean pvpForcePVPSettingsOn;
	public boolean pvpOverrideEnabled;
	
	public boolean pvpPluginDisable;
	
	private DamageListener dL = new DamageListener(this);
	private PlayerEventListener pEL = new PlayerEventListener(this);
	// Getting some logging done.
	protected final Logger log = Logger.getLogger("Minecraft");
	
	
	@Override
	public void onDisable() {
		// Disable all running timers.
		Bukkit.getServer().getScheduler().cancelTasks(this);
		
		log.info(getDescription().getName() + ": has been disabled (including timers).");
	}

	@Override
	public void onEnable() {
		log.info(getServer().getWorlds().toString());
		if(getServer().getWorld(pvpWorldName) == null) {
			log.info("[" + getDescription().getName() + "] version " + getDescription().getVersion() + " is disabled due to wrong world name in config.yml" );
			pvpPluginDisable = true;
		} else {
			pvpPluginDisable = false;
			
			log.info("[" + getDescription().getName() + "] version " + getDescription().getVersion() + " is enabled." );
			
			// Checking if we start with pvpTime
			long worldTime = getServer().getWorld(pvpWorldName).getTime();
			if(dL.isItPvPTime(worldTime)) {
				pvpAnnouncedPvP = true;
			} else {
				pvpAnnouncedPvP = false;
			}
		}
		
		if(pvpPluginDisable == false) {
			PluginManager pm = this.getServer().getPluginManager();
			pm.registerEvent(Event.Type.ENTITY_DAMAGE, (Listener) dL, Event.Priority.Low, this);
			pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, (Listener) pEL, Event.Priority.Low, this);
			
			// Are we forcing the pvp setting on?
			if(pvpForcePVPSettingsOn == true) {
				getServer().getWorld(pvpWorldName).setPVP(true);
			}
			
			if(pvpEndMsgBroadcast == true || pvpStartMsgBroadcast == true) {
				checkTime();
			}
		}
		
	}
	
	@Override
	public void onLoad() {
		getDataFolder().mkdirs();
		reloadConfig();
		
	}
	
	public void reloadConfig() {
		Configuration config = getConfiguration();
		config.load();
		pvpStartTime = config.getInt("pvp.start.time", 13000);
		pvpStartMsg = config.getString("pvp.start.msg.text", "It's night and PvP is turned on");
		pvpStartMsgColor = config.getString("pvp.start.msg.color", "DARK_RED");
		pvpStartMsgBroadcast = config.getBoolean("pvp.start.msg.broadcast", true);
		pvpEndTime = config.getInt("pvp.end.time", 1000);
		pvpEndMsg = config.getString("pvp.end.msg.text", "It's daytime and PvP is turned off");
		pvpEndMsgColor = config.getString("pvp.end.msg.color", "GREEN");
		pvpEndMsgBroadcast = config.getBoolean("pvp.end.msg.broadcast", true);
		pvpWorldName = config.getString("pvp.world.name", "world");
		pvpForcePVPSettingsOn = config.getBoolean("pvp.force.on", true);
		pvpOverrideEnabled = config.getBoolean("pvp.override.enabled", false);
		config.save();
	}
	
	public void checkTime() {
		// is it pvp on now?
		long worldTime = getServer().getWorld(pvpWorldName).getTime();
		long timeLeft = 0;
		if(dL.isItPvPTime(worldTime)) {
			// it's pvp time, but have we announced it?
			if(pvpAnnouncedPvP == false) {
				announceNow(true);
			}
			// Check how long until pvp is over.
			if(worldTime > pvpEndTime) {
				timeLeft = 24000 - worldTime + pvpEndTime;
			} else {
				timeLeft = pvpEndTime - worldTime;
			}
		} else {
			// it's not pvp time, but have we announced it?
			if(pvpAnnouncedPvP == true) {
				announceNow(false);
			}
			if(worldTime > pvpStartTime) {
				timeLeft = 24000 - worldTime + pvpStartTime;
			} else {
				timeLeft = pvpStartTime - worldTime;
			}
		}
		checkTimeClock(Math.round(timeLeft/3+1));
	}
	
	public void checkTimeClock(long countdown) {
		getServer().getScheduler().cancelTasks(this);
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
				public void run() {
					checkTime();
				}
			}, countdown);
	}
	
	public void announceNow (boolean pvpOn) {
		if(pvpOn) {
			if(pvpStartMsgBroadcast) {
				getServer().broadcastMessage(getColorFromString(pvpStartMsgColor) + pvpStartMsg);
			}
			pvpAnnouncedPvP = true;			
		} else {
			if(pvpEndMsgBroadcast) {
				getServer().broadcastMessage(getColorFromString(pvpEndMsgColor) + pvpEndMsg);
			}
			pvpAnnouncedPvP = false;
		}
	}
	
	public ChatColor getColorFromString(String color) {
		if(color.equalsIgnoreCase("BLACK")) {
			return ChatColor.BLACK;
		}else if(color.equalsIgnoreCase("DARK_BLUE")) {
			return ChatColor.DARK_BLUE;
		}else if(color.equalsIgnoreCase("DARK_AQUA")) {
			return ChatColor.DARK_AQUA;
		}else if(color.equalsIgnoreCase("DARK_RED")) {
			return ChatColor.DARK_RED;
		}else if(color.equalsIgnoreCase("DARK_PURPLE")) {
			return ChatColor.DARK_PURPLE;
		}else if(color.equalsIgnoreCase("GOLD")) {
			return ChatColor.GOLD;
		}else if(color.equalsIgnoreCase("GRAY")) {
			return ChatColor.GRAY;
		}else if(color.equalsIgnoreCase("DARK_GRAY")) {
			return ChatColor.DARK_GRAY;
		}else if(color.equalsIgnoreCase("BLUE")) {
			return ChatColor.BLUE;
		}else if(color.equalsIgnoreCase("GREEN")) {
			return ChatColor.GREEN;
		}else if(color.equalsIgnoreCase("AQUA")) {
			return ChatColor.AQUA;
		}else if(color.equalsIgnoreCase("RED")) {
			return ChatColor.RED;
		}else if(color.equalsIgnoreCase("LIGHT_PURPLE")) {
			return ChatColor.LIGHT_PURPLE;
		}else if(color.equalsIgnoreCase("YELLOW")) {
			return ChatColor.YELLOW;
		}else if(color.equalsIgnoreCase("WHITE")) {
			return ChatColor.WHITE;
		}		
		return ChatColor.WHITE;
	}
	/*
	// Stolen from ltguide! Thank you so much :)
	public Boolean hasPermission(CommandSender sender, String node, Boolean needOp) {
		if (!(sender instanceof Player)) return true;
	
		Player player = (Player) sender;
		if (Permissions != null) return Permissions.has(player, node);
		else {
			Plugin test = getServer().getPluginManager().getPlugin("Permissions");
			if (test != null) {
				Permissions = ((Permissions) test).getHandler();
				return Permissions.has(player, node);
			}
		}
		if(needOp) {
			return player.isOp();
		}
		return true;
	}
         */
         

}
