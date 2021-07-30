// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.events;

import org.bukkit.event.EventHandler;
import me.poutineqc.deacoudre.instances.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.Listener;

public class ElytraToggle implements Listener
{
    @EventHandler
    public void onElytraToggle(final EntityToggleGlideEvent event) {
        if (!event.isGliding()) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getEntity();
        final Arena arena = Arena.getArenaFromPlayer(player);
        if (arena == null) {
            return;
        }
        event.setCancelled(true);
    }
}
