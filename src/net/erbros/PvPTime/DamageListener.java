package net.erbros.PvPTime;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class DamageListener extends EntityListener {
    private PvPTime plugin;
    
    DamageListener(PvPTime instance) {
    	plugin = instance;
    }
    
	public void onEntityDamage(EntityDamageEvent event) {
		
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entEvent = (EntityDamageByEntityEvent)event;
			// check if both are players. thanks LRFLEW :)
			if(entEvent.getDamager() instanceof Player && entEvent.getEntity() instanceof Player) {
				Player attacker = (Player) entEvent.getDamager();
				// What time is it? Thanks to sk89q :)
				World world = attacker.getWorld();
				long getTimeWorld = world.getTime();
				attacker.sendMessage(String.valueOf(getTimeWorld));
				//two different solutions depending on which of the times are higher.
				
				if(getTimeWorld > plugin.pvpStartTime || getTimeWorld < plugin.pvpEndTime)
				{
					// What to do as pvp is clearly on.
				}
				else
				{
					// Give some info that pvp is off during day? Custom.
					attacker.sendMessage(plugin.pvpEndMsg);
					// Cancel the event.
					event.setCancelled(true);
				}
			}
			
		}
	}
	
	public boolean isItPvPTime(long time) {
		if(plugin.pvpStartTime < plugin.pvpEndTime) {
			if(time > plugin.pvpStartTime && time < plugin.pvpEndTime) {
				return true;
			} else {
				return false;
			}
		} else {
			if(time > plugin.pvpStartTime || time < plugin.pvpEndTime) {
				return true;
			} else {
				return false;
			}
		}
	}
}