// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.events;

import me.poutineqc.deacoudre.DeACoudre;
import org.bukkit.event.EventHandler;
import me.poutineqc.deacoudre.instances.User;
import org.bukkit.entity.Player;
import me.poutineqc.deacoudre.instances.Arena;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;

public class PlayerDisconnect implements Listener
{
    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Arena arena = Arena.getArenaFromPlayer(player);
        if (arena == null) {
            return;
        }
        final User user = arena.getUser(player);
        arena.removeUserFromGame(user, true);
        DeACoudre.selections.remove(player.getUniqueId());
    }
}
