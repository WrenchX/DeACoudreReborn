// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.tools;

import org.bukkit.potion.PotionEffectType;
import me.poutineqc.deacoudre.instances.Arena;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import java.util.Collection;
import org.bukkit.GameMode;
import me.poutineqc.deacoudre.Configuration;

public class OriginalPlayerStats
{
    private Configuration config;
    private int level;
    private float experience;
    private GameMode gameMode;
    private double health;
    private int foodLevel;
    private float saturation;
    private Collection<PotionEffect> effects;
    private Location location;
    private boolean flying;
    private boolean allowFlight;
    
    public OriginalPlayerStats(final Configuration config, final Player player) {
        this.config = config;
        this.location = player.getLocation();
    }
    
    public void returnStats(final Player player) {
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setAllowFlight(this.allowFlight);
        if (this.flying) {
            player.setAllowFlight(true);
            player.setFlying(this.flying);
        }
        player.setFallDistance(0.0f);
        player.setVelocity(new Vector());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        player.setLevel(this.level);
        player.setExp(this.experience);
        player.setGameMode(this.gameMode);
        player.setHealth(this.health);
        player.setFoodLevel(this.foodLevel);
        player.setSaturation(this.saturation);
        player.addPotionEffects((Collection)this.effects);
        if (this.config.teleportAfterEnding) {
            player.teleport(this.location);
        }
        else if (this.config.invisibleFlyingSpectators) {
            player.setFallDistance(-255.0f);
        }
    }
    
    public void maxStats(final Player player, final Arena arena, final boolean spectator) {
        player.setFallDistance(0.0f);
        player.setVelocity(new Vector());
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setLevel(0);
        player.setExp(0.0f);
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (this.config.invisibleFlyingSpectators) {
            this.spectatorStats(player, arena, spectator);
        }
    }
    
    public void spectatorStats(final Player player, final Arena arena, final boolean spectator) {
        player.setAllowFlight(spectator);
        if (spectator) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000, 1, false, false));
            if (!arena.getSpectator().hasEntry(player.getName())) {
                arena.getSpectator().addEntry(player.getName());
            }
        }
        else {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
            if (arena.getSpectator().hasEntry(player.getName())) {
                arena.getSpectator().removeEntry(player.getName());
            }
        }
    }
    
    public void fillOtherStats(final Player player) {
        this.flying = player.isFlying();
        this.allowFlight = player.getAllowFlight();
        this.level = player.getLevel();
        this.experience = player.getExp();
        this.gameMode = player.getGameMode();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.effects = (Collection<PotionEffect>)player.getActivePotionEffects();
    }
}
