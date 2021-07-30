// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.events;

import org.bukkit.event.EventHandler;
import me.poutineqc.deacoudre.Language;
import org.bukkit.entity.Player;
import me.poutineqc.deacoudre.instances.GameState;
import me.poutineqc.deacoudre.instances.Arena;
import org.bukkit.event.player.PlayerTeleportEvent;
import me.poutineqc.deacoudre.DeACoudre;
import me.poutineqc.deacoudre.PlayerData;
import org.bukkit.event.Listener;

public class PlayerTeleport implements Listener
{
    private PlayerData playerData;
    
    public PlayerTeleport(final DeACoudre plugin) {
        this.playerData = plugin.getPlayerData();
    }
    
    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final Arena arena = Arena.getArenaFromPlayer(player);
        if (arena == null) {
            return;
        }
        if (arena.getGameState() == GameState.ENDING) {
            return;
        }
        if (event.getTo().getWorld() == arena.getWorld() && (event.getTo().distance(arena.getPlateform()) < 1.0 || event.getTo().distance(arena.getLobby()) < 1.0)) {
            return;
        }
        event.setCancelled(true);
        final Language local = this.playerData.getLanguageOfPlayer(player);
        local.sendMsg(player, local.errorTeleport);
    }
}
