// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.events;

import java.util.Iterator;
import me.poutineqc.deacoudre.Language;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import me.poutineqc.deacoudre.instances.User;
import me.poutineqc.deacoudre.instances.GameState;
import me.poutineqc.deacoudre.instances.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import me.poutineqc.deacoudre.DeACoudre;
import me.poutineqc.deacoudre.PlayerData;
import org.bukkit.event.Listener;

public class PlayerDamage implements Listener
{
    private PlayerData playerData;
    
    public PlayerDamage(final DeACoudre plugin) {
        this.playerData = plugin.getPlayerData();
    }
    
    @EventHandler
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getEntity();
        final Arena arena = Arena.getArenaFromPlayer(player);
        if (arena == null) {
            return;
        }
        if (arena.getGameState() != GameState.ACTIVE) {
            return;
        }
        event.setCancelled(true);
        if (!event.getCause().equals((Object)EntityDamageEvent.DamageCause.FALL)) {
            return;
        }
        final User players = arena.getUser(player);
        if (players == arena.getActivePlayer()) {
            this.losingAlgorithm(player, arena, players);
        }
    }
    
    public void losingAlgorithm(final Player player, final Arena arena, final User user) {
        final Language local = this.playerData.getLanguageOfPlayer(player);
        user.removePoint();
        player.teleport(arena.getLobby());
        user.maxStats(true);
        arena.getScoreboard().resetScores(ChatColor.AQUA + user.getName());
        arena.getObjective().getScore(user.getName()).setScore(user.getPoint());
        arena.bumpStallingAmount();
        if (arena.isForceStart()) {
            if (user.getPoint() == -1) {
                user.eliminate();
                local.sendMsg(player, local.gamePointsEliminatePlayer);
                for (final User u : arena.getUsers()) {
                    if (user != u) {
                        final Language localInstance = this.playerData.getLanguageOfPlayer(u.getPlayer());
                        localInstance.sendMsg(u.getPlayer(), localInstance.gamePointsEliminateOthers.replace("%player%", player.getDisplayName()));
                    }
                }
                arena.finishGame(false);
            }
            else {
                arena.nextPlayer();
                local.sendMsg(player, local.gamePointsDownPlayer.replace("%points%", String.valueOf(user.getPoint())));
                for (final User u : arena.getUsers()) {
                    if (user != u) {
                        final Language localInstance = this.playerData.getLanguageOfPlayer(u.getPlayer());
                        localInstance.sendMsg(u.getPlayer(), localInstance.gamePointsDownOthers.replace("%player%", player.getDisplayName()).replace("%points%", String.valueOf(user.getPoint())));
                    }
                }
            }
            return;
        }
        if (arena.isSomeoneSurvived()) {
            if (user.getPoint() == -1) {
                user.eliminate();
                local.sendMsg(player, local.gamePointsEliminatePlayer);
                for (final User u : arena.getUsers()) {
                    if (user != u) {
                        final Language localInstance = this.playerData.getLanguageOfPlayer(u.getPlayer());
                        localInstance.sendMsg(u.getPlayer(), localInstance.gamePointsEliminateOthers.replace("%player%", player.getDisplayName()));
                    }
                }
            }
            else {
                local.sendMsg(player, local.gamePointsDownPlayer.replace("%points%", String.valueOf(user.getPoint())));
                for (final User u : arena.getUsers()) {
                    if (user != u) {
                        final Language localInstance = this.playerData.getLanguageOfPlayer(u.getPlayer());
                        localInstance.sendMsg(u.getPlayer(), localInstance.gamePointsDownOthers.replace("%player%", player.getDisplayName()).replace("%points%", String.valueOf(user.getPoint())));
                    }
                }
            }
        }
        else if (arena.isLastPlayer(user)) {
            if (user.getPoint() == -1) {
                user.addPoint();
                arena.setSomeoneLostFinal(true);
                local.sendMsg(player, local.gamePointsReviveLastLastPlayer);
                for (final User p : arena.getUsers()) {
                    if (p != user) {
                        final Language localInstance = this.playerData.getLanguageOfPlayer(p.getPlayer());
                        localInstance.sendMsg(p.getPlayer(), localInstance.gamePointsReviveLastLastOthers.replace("%player%", player.getDisplayName()));
                    }
                }
            }
            else if (arena.isSomeoneLostFinal()) {
                user.addWaitingForConfirmation();
                local.sendMsg(player, local.gamePointsReviveLastMultiplePlayer);
                for (final User p : arena.getUsers()) {
                    if (p != user) {
                        final Language localInstance = this.playerData.getLanguageOfPlayer(p.getPlayer());
                        localInstance.sendMsg(p.getPlayer(), localInstance.gamePointsReviveLastMultipleOthers.replace("%player%", player.getDisplayName()));
                    }
                }
            }
            else {
                local.sendMsg(player, local.gamePointsDownPlayer.replace("%points%", String.valueOf(user.getPoint())));
                for (final User p : arena.getUsers()) {
                    if (user != p) {
                        final Language localInstance = this.playerData.getLanguageOfPlayer(p.getPlayer());
                        localInstance.sendMsg(p.getPlayer(), localInstance.gamePointsDownOthers.replace("%player%", player.getDisplayName()).replace("%points%", String.valueOf(user.getPoint())));
                    }
                }
            }
        }
        else if (user.getPoint() == -1) {
            user.addWaitingForConfirmation();
            arena.setSomeoneLostFinal(true);
            local.sendMsg(player, local.gamePointsConfirmationPlayer);
            for (final User p : arena.getUsers()) {
                if (p != user) {
                    final Language localInstance = this.playerData.getLanguageOfPlayer(p.getPlayer());
                    localInstance.sendMsg(p.getPlayer(), localInstance.gamePointsConfirmationOthers.replace("%player%", player.getDisplayName()));
                }
            }
        }
        else {
            user.addWaitingForConfirmation();
            local.sendMsg(player, local.gamePointsDownPlayer.replace("%points%", String.valueOf(user.getPoint())));
            local.sendMsg(player, local.gamePointsReviveHint);
            for (final User p : arena.getUsers()) {
                if (user != p) {
                    final Language localInstance = this.playerData.getLanguageOfPlayer(p.getPlayer());
                    localInstance.sendMsg(p.getPlayer(), localInstance.gamePointsDownOthers.replace("%player%", player.getDisplayName()).replace("%points%", String.valueOf(user.getPoint())));
                }
            }
        }
        if (arena.isLastPlayer(user) && arena.isSomeoneLostFinal()) {
            arena.reviveConfirmationQueue();
        }
        if (arena.isOver()) {
            arena.finishGame(false);
        }
        else {
            arena.nextPlayer();
        }
    }
}
