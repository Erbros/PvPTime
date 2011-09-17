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
    
    @Override
    public void onEntityDamage(EntityDamageEvent event) {

            if(event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entEvent = (EntityDamageByEntityEvent)event;

                // check if both are players. thanks LRFLEW :)
                if(entEvent.getDamager() instanceof Player && entEvent.getEntity() instanceof Player) {
                    // Do we have a player with pvp override?
                Player attacker = (Player) entEvent.getDamager();
                if(plugin.pvpOverrideEnabled == true) {
                    if(attacker.hasPermission("pvptime.override")) {
                            return;
                    }
                }

                // What time is it? Thanks to sk89q :)
                World world = attacker.getWorld();
                long getTimeWorld = world.getTime();
                //attacker.sendMessage(String.valueOf(getTimeWorld));
                //two different solutions depending on which of the times are higher.

                if(isItPvPTime(getTimeWorld) == false) {
                    event.setCancelled(true);
                }
                return;
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