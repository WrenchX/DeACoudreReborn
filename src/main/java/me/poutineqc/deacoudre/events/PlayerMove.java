// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.events;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import java.sql.ResultSet;
import me.poutineqc.deacoudre.Language;
import org.bukkit.Material;
import java.sql.SQLException;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import me.poutineqc.deacoudre.instances.User;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import me.poutineqc.deacoudre.instances.GameState;
import me.poutineqc.deacoudre.instances.Arena;
import org.bukkit.event.player.PlayerMoveEvent;
import me.poutineqc.deacoudre.Configuration;
import me.poutineqc.deacoudre.MySQL;
import me.poutineqc.deacoudre.achievements.Achievement;
import me.poutineqc.deacoudre.PlayerData;
import me.poutineqc.deacoudre.DeACoudre;
import org.bukkit.event.Listener;

public class PlayerMove implements Listener
{
    private DeACoudre plugin;
    private PlayerData playerData;
    private Achievement achievements;
    private MySQL mysql;
    private Configuration config;
    
    public PlayerMove(final DeACoudre plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.mysql = plugin.getMySQL();
        this.achievements = plugin.getAchievement();
        this.playerData = plugin.getPlayerData();
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Arena arena = Arena.getArenaFromPlayer(player);
        if (arena == null) {
            return;
        }
        if (arena.getGameState() != GameState.ACTIVE) {
            return;
        }
        final User user = arena.getUser(player);
        if (user != arena.getActivePlayer()) {
            return;
        }
        final Location getTo = new Location(event.getTo().getWorld(), (double)event.getTo().getBlockX(), (double)event.getTo().getBlockY(), (double)event.getTo().getBlockZ());
        if (!getTo.getBlock().isLiquid()) {
            return;
        }
        while (getTo.add(new Vector(0, 1, 0)).getBlock().isLiquid()) {}
        getTo.add(new Vector(0, -1, 0));
        final Location north = new Location(getTo.getWorld(), (double)getTo.getBlockX(), (double)getTo.getBlockY(), (double)(getTo.getBlockZ() - 1));
        final Location south = new Location(getTo.getWorld(), (double)getTo.getBlockX(), (double)getTo.getBlockY(), (double)(getTo.getBlockZ() + 1));
        final Location east = new Location(getTo.getWorld(), (double)(getTo.getBlockX() + 1), (double)getTo.getBlockY(), (double)getTo.getBlockZ());
        final Location west = new Location(getTo.getWorld(), (double)(getTo.getBlockX() - 1), (double)getTo.getBlockY(), (double)getTo.getBlockZ());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                player.teleport(arena.getLobby());
                final Arena arena = Arena.getArenaFromPlayer(player);
                if (arena == null) {
                    return;
                }
                user.maxStats(true);
                if (arena.getGameState() == GameState.ACTIVE) {
                    arena.getScoreboard().resetScores(ChatColor.AQUA + user.getName());
                    arena.getObjective().getScore(user.getName()).setScore(user.getPoint());
                }
            }
        }, 5L);
        final Language local = this.playerData.getLanguageOfPlayer(player);
        arena.resetStallingAmount();
        arena.bumpCurrentTile();
        if (!north.getBlock().isLiquid() && !south.getBlock().isLiquid() && !west.getBlock().isLiquid() && !east.getBlock().isLiquid()) {
            user.addPoint();
            int DaCdone = 0;
            if (this.mysql.hasConnection()) {
                final ResultSet query = this.mysql.query("SELECT DaCdone FROM " + this.config.tablePrefix + "PLAYERS WHERE UUID='" + user.getUUID() + "';");
                try {
                    if (query.next()) {
                        DaCdone = query.getInt("DaCdone");
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                this.mysql.update("UPDATE " + this.config.tablePrefix + "PLAYERS SET DaCdone='" + ++DaCdone + "' WHERE UUID='" + user.getUUID() + "';");
            }
            else {
                DaCdone = this.playerData.getData().getInt("players." + player.getUniqueId() + ".DaCdone") + 1;
                this.playerData.getData().set("players." + player.getUniqueId() + ".DaCdone", (Object)DaCdone);
                this.playerData.savePlayerData();
            }
            if (!arena.isForceStart()) {
                this.achievements.testAchievement(".DaCdone", player);
                if (arena.getRoundNo() == 42) {
                    this.achievements.testAchievement(".challenges.DaCon42", player);
                }
            }
            local.sendMsg(user.getPlayer(), local.gamePointsUpPlayer.replace("%points%", String.valueOf(user.getPoint())));
            for (final User u : arena.getUsers()) {
                if (u != user) {
                    final Language localInstance = this.playerData.getLanguageOfPlayer(u.getPlayer());
                    localInstance.sendMsg(u.getPlayer(), localInstance.gamePointsUpOthers.replace("%points%", String.valueOf(user.getPoint())).replace("%player%", player.getDisplayName()));
                }
            }
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    do {
                        getTo.getBlock().setType(Material.STAINED_GLASS);
                        getTo.getBlock().setData((byte)user.getItemStack().getDurability());
                        getTo.add(0.0, -1.0, 0.0);
                    } while (getTo.getBlock().getType() == Material.WATER);
                }
            }, 5L);
        }
        else {
            if (this.config.verbose) {
                local.sendMsg(user.getPlayer(), local.gameSuccessPlayer);
                for (final User op : arena.getUsers()) {
                    if (op != user) {
                        final Language localInstance2 = this.playerData.getLanguageOfPlayer(op.getPlayer());
                        localInstance2.sendMsg(op.getPlayer(), localInstance2.gameSuccessOthers.replace("%player%", player.getDisplayName()));
                    }
                }
            }
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    do {
                        getTo.getBlock().setType(user.getItemStack().getType());
                        getTo.getBlock().setData((byte)user.getItemStack().getDurability());
                        getTo.add(0.0, -1.0, 0.0);
                    } while (getTo.getBlock().getType() == Material.WATER);
                }
            }, 5L);
        }
        user.setRoundSuccess(true);
        arena.flushConfirmationQueue(user);
        if (!arena.isOver() || arena.isForceStart()) {
            arena.nextPlayer();
        }
        else {
            user.getPlayer().setVelocity(new Vector());
            user.getPlayer().setFallDistance(0.0f);
            arena.finishGame(false);
        }
    }
}
