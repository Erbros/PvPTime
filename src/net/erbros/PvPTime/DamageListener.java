package net.erbros.PvPTime;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {
    private PvPTime plugin;
    
    DamageListener(PvPTime instance) {
    	plugin = instance;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {

        if(event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entEvent = (EntityDamageByEntityEvent)event;

            // check if both are players. thanks LRFLEW :)
            if(entEvent.getDamager() instanceof Player && entEvent.getEntity() instanceof Player) {
                // Do we have a player with pvp override?
                Player attacker = (Player) entEvent.getDamager();
                if((Boolean) plugin.getValue(plugin.pvpWorlds, attacker.getWorld().getName(), "overrideEnabled") == true) {
                    if(attacker.hasPermission("pvptime.override")) {
                            return;
                    }
                }

                if(isItPvPTime(attacker.getWorld().getName()) == false) {
                    event.setCancelled(true);
                }
                // What if this is an arrow?
            } else if (entEvent.getDamager() instanceof Projectile && entEvent.getEntity() instanceof Player) {
                
                Projectile projectile = (Projectile) entEvent.getDamager();
                // Is this a player or?
                if(projectile.getShooter() instanceof Player) {
                    Player attacker = (Player) projectile.getShooter();
                    // Do we have a player with pvp override?
                    if((Boolean) plugin.getValue(plugin.pvpWorlds, attacker.getWorld().getName(), "overrideEnabled") == true) {
                        if(attacker.hasPermission("pvptime.override")) {
                                return;
                        }
                    }
                    
                    if(isItPvPTime(attacker.getWorld().getName()) == false) {
                        event.setCancelled(true);
                    }
                    
                }
            }
            return;
        } 
    }

    public boolean isItPvPTime(String world) {
        long startTime = Long.parseLong(plugin.getValue(plugin.pvpWorlds, world, "startTime").toString());
        long endTime = Long.parseLong(plugin.getValue(plugin.pvpWorlds, world, "endTime").toString());
        if(startTime < endTime) {
            if( plugin.getServer().getWorld(world).getTime() > startTime && plugin.getServer().getWorld(world).getTime() < endTime) {
                    return true;
            } else {
                    return false;
            }
    } else {
            if(plugin.getServer().getWorld(world).getTime() > startTime || plugin.getServer().getWorld(world).getTime() < endTime) {
                    return true;
            } else {
                    return false;
            }
        }
    }
}