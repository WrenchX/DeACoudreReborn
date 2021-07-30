// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.instances;

import java.util.UUID;
import org.bukkit.ChatColor;
import me.poutineqc.deacoudre.DeACoudre;
import org.bukkit.scoreboard.Score;
import org.bukkit.inventory.ItemStack;
import me.poutineqc.deacoudre.tools.OriginalPlayerStats;
import org.bukkit.entity.Player;
import me.poutineqc.deacoudre.Configuration;

public class User
{
    private static Configuration config;
    private Player player;
    private String displayName;
    private String name;
    private OriginalPlayerStats originalStats;
    private ItemStack color;
    private int points;
    private int place;
    private boolean roundSuccess;
    private boolean eliminated;
    private boolean waitingForConfirmation;
    private Score score;
    private Arena arena;
    
    public User(final DeACoudre plugin) {
        this.points = 0;
        this.roundSuccess = false;
        this.eliminated = false;
        this.waitingForConfirmation = false;
        User.config = plugin.getConfiguration();
    }
    
    public User(final Player player, final Arena arena, final boolean tpAuto, final boolean eliminated) {
        this.points = 0;
        this.roundSuccess = false;
        this.eliminated = false;
        this.waitingForConfirmation = false;
        this.player = player;
        this.arena = arena;
        this.name = ChatColor.stripColor(player.getName());
        this.displayName = player.getDisplayName();
        this.eliminated = eliminated;
        if (eliminated) {
            this.points = -2;
        }
        else {
            (this.score = arena.getObjective().getScore(this.name)).setScore(this.points);
        }
        player.setScoreboard(arena.getObjective().getScoreboard());
        this.originalStats = new OriginalPlayerStats(User.config, player);
        if (tpAuto) {
            player.teleport(arena.getLobby());
        }
        this.originalStats.fillOtherStats(player);
        this.maxStats(false);
    }
    
    public void unEliminate(final Arena arena) {
        this.eliminated = false;
        this.points = 0;
        (this.score = arena.getObjective().getScore(this.name)).setScore(this.points);
    }
    
    public User(final int place) {
        this.points = 0;
        this.roundSuccess = false;
        this.eliminated = false;
        this.waitingForConfirmation = false;
        this.place = place;
    }
    
    public User(final String name, final int place) {
        this.points = 0;
        this.roundSuccess = false;
        this.eliminated = false;
        this.waitingForConfirmation = false;
        this.place = place;
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getPlace() {
        return this.place;
    }
    
    public void setPlace(final int place) {
        this.place = place;
    }
    
    public void setColor(final ItemStack item) {
        this.color = item;
    }
    
    public void removeColor() {
        this.color = null;
    }
    
    public ItemStack getItemStack() {
        return this.color;
    }
    
    public UUID getUUID() {
        return this.player.getUniqueId();
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void addPoint() {
        this.score.setScore(++this.points);
    }
    
    public void removePoint() {
        this.score.setScore(--this.points);
    }
    
    public int getPoint() {
        return this.points;
    }
    
    public boolean isRoundSuccess() {
        return this.roundSuccess;
    }
    
    public void setRoundSuccess(final boolean roundSuccess) {
        this.roundSuccess = roundSuccess;
    }
    
    public void returnOriginalPlayerStats() {
        this.originalStats.returnStats(this.player);
    }
    
    public boolean isEliminated() {
        return this.eliminated;
    }
    
    public void eliminate() {
        this.eliminated = true;
        this.score.getObjective().getScoreboard().resetScores(this.name);
        this.score.getObjective().getScoreboard().resetScores(ChatColor.AQUA + this.name);
        this.points = -2;
    }
    
    public boolean isWaitingForConfirmation() {
        if (this.waitingForConfirmation) {
            this.waitingForConfirmation = false;
            return true;
        }
        return false;
    }
    
    public void addWaitingForConfirmation() {
        this.waitingForConfirmation = true;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void maxStats(final boolean spectator) {
        this.originalStats.maxStats(this.player, this.arena, spectator);
    }
}
