package com.golfing8.profiler.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

/**
 * Listens to world events.
 */
public class WorldListener implements Listener {
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        // All creature spawn events should be suppressed.
        event.setCancelled(true);
    }
}
