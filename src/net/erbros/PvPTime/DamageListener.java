package net.erbros.PvPTime;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;

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
                    plugin.debugMsg("Override enabled in " + attacker.getWorld().getName());
                    if(attacker.hasPermission("pvptime.override")) {
                        plugin.debugMsg(attacker.getName() + " had permission to override PvPTime");
                            return;
                    }
                }

                if(isItPvPTime(attacker.getWorld().getName()) == false) {
                    plugin.debugMsg("No pvp. Cancelled: " + attacker.getName() + " in " + attacker.getWorld().getName() + ". Regular.");
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
                        plugin.debugMsg("Override enabled in " + attacker.getWorld().getName());
                        if(attacker.hasPermission("pvptime.override")) {
                            plugin.debugMsg(attacker.getName() + " had permission to override PvPTime");
                                return;
                        }
                    }
                    
                    if(isItPvPTime(attacker.getWorld().getName()) == false) {
                        plugin.debugMsg("No pvp. Cancelled: " + attacker.getName() + " in " + attacker.getWorld().getName() + ". Arrow.");
                        event.setCancelled(true);
                    }
                    
                }
            } 
            return;
        } 
    }
    
    // What about potions? Don't let splash potions be throw 
    @EventHandler(priority = EventPriority.LOW)
    public void onPotionSplash(PotionSplashEvent event) {
        plugin.debugMsg("Potion Splash event");
        // who is the attacker?
        if(event.getEntity().getShooter() instanceof Player) {
            Player attacker = (Player) event.getEntity().getShooter();
            // is there a player in the affected area?
            Collection<LivingEntity> affected = event.getAffectedEntities();
            boolean onlyAnimals = true;
            plugin.debugMsg("Checking splash event. " + affected.size() + " entities.");
            for(Iterator<LivingEntity> iter = affected.iterator(); iter.hasNext() && onlyAnimals;) {
                plugin.debugMsg("Is this entity a player?");
                if(iter.next() instanceof Player)
                    // We found a player! Not only animals affected.
                    onlyAnimals = false;
            }
            // Did we find any players? No? then just end.
            if(onlyAnimals) {
                plugin.debugMsg("Stopped looking at event, no players hit");
                return;
            }
            
            // Do we have a player with pvp override?
            if((Boolean) plugin.getValue(plugin.pvpWorlds, attacker.getWorld().getName(), "overrideEnabled") == true) {
                plugin.debugMsg("Override enabled in " + attacker.getWorld().getName());
                if(attacker.hasPermission("pvptime.override")) {
                    plugin.debugMsg(attacker.getName() + " had permission to override PvPTime");
                        return;
                }
            }
            
            if(isItPvPTime(attacker.getWorld().getName()) == false) {
                plugin.debugMsg("No pvp. Cancelled: " + attacker.getName() + " in " + attacker.getWorld().getName() + ". Potion.");
                event.setCancelled(true);
            }
            
        }
    }
    
    

    public boolean isItPvPTime(String world) {
        long startTime = Long.parseLong(plugin.getValue(plugin.pvpWorlds, world, "startTime").toString());
        long endTime = Long.parseLong(plugin.getValue(plugin.pvpWorlds, world, "endTime").toString());
        long currentTime = plugin.getServer().getWorld(world).getTime();
        if(startTime < endTime) {
            if( currentTime > startTime && currentTime < endTime) {
                    return true;
            } else {
                    return false;
            }
        } else {
            if(currentTime > startTime || currentTime < endTime) {
                    return true;
            } else {
                    return false;
            }
        }
    }
}