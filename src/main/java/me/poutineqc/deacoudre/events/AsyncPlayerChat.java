// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.events;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import me.poutineqc.deacoudre.instances.GameState;
import org.bukkit.entity.Player;
import me.poutineqc.deacoudre.instances.Arena;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import me.poutineqc.deacoudre.DeACoudre;
import org.bukkit.event.Listener;

public class AsyncPlayerChat implements Listener
{
    private DeACoudre plugin;
    
    public AsyncPlayerChat(final DeACoudre plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent e) {
        if (!this.plugin.getConfiguration().chatRooms) {
            return;
        }
        final Player player = e.getPlayer();
        final Arena arena = Arena.getArenaFromPlayer(player);
        if (arena == null) {
            for (final Player p : Arena.getAllPlayersInStartedGame()) {
                e.getRecipients().remove(p);
            }
            return;
        }
        if (arena.getGameState() != GameState.ACTIVE) {
            for (final Player p : Arena.getAllPlayersInStartedGame()) {
                e.getRecipients().remove(p);
            }
            return;
        }
        for (final Player p : Arena.getAllOutsideGame(arena)) {
            e.getRecipients().remove(p);
        }
    }
}
