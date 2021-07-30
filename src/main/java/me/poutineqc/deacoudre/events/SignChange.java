// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.events;

import org.bukkit.event.EventHandler;
import me.poutineqc.deacoudre.commands.SignType;
import me.poutineqc.deacoudre.commands.DacSign;
import me.poutineqc.deacoudre.instances.Arena;
import me.poutineqc.deacoudre.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.event.block.SignChangeEvent;
import me.poutineqc.deacoudre.Language;
import me.poutineqc.deacoudre.DeACoudre;
import me.poutineqc.deacoudre.PlayerData;
import me.poutineqc.deacoudre.Configuration;
import org.bukkit.event.Listener;

public class SignChange implements Listener
{
    private Configuration config;
    private PlayerData playerData;
    
    public SignChange(final DeACoudre plugin, final Language local) {
        this.config = plugin.getConfiguration();
        this.playerData = plugin.getPlayerData();
    }
    
    @EventHandler
    public void onSignChange(final SignChangeEvent e) {
        final Language local = this.playerData.getLanguage(this.config.language);
        if (this.isPrefixInLine(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', e.getLine(0))).toLowerCase(), local)) {
            if (!Permissions.hasPermission(e.getPlayer(), "dac.admin.makesigns", false)) {
                this.setSignNoPermissions(e, local);
                return;
            }
            if (e.getLine(1).equalsIgnoreCase("join")) {
                final Arena arena = Arena.getArenaFromName(e.getLine(2));
                if (arena != null) {
                    new DacSign(e, SignType.JOIN);
                }
                else {
                    this.setNoValidSign(e, local);
                }
            }
            else if (e.getLine(1).equalsIgnoreCase("play")) {
                final Arena arena = Arena.getArenaFromName(e.getLine(2));
                if (arena != null) {
                    if (arena.getWorld() == null) {
                        this.setNoValidSign(e, local);
                    }
                    else if (arena.getWorld() != e.getBlock().getWorld()) {
                        this.setNoValidSign(e, local);
                    }
                    else {
                        new DacSign(e, SignType.PLAY);
                    }
                }
                else {
                    this.setNoValidSign(e, local);
                }
            }
            else if (e.getLine(1).equalsIgnoreCase("quit")) {
                new DacSign(e, SignType.QUIT);
            }
            else if (e.getLine(1).equalsIgnoreCase("color")) {
                new DacSign(e, SignType.COLOR);
            }
            else if (e.getLine(1).equalsIgnoreCase("start")) {
                new DacSign(e, SignType.START);
            }
            else if (e.getLine(1).equalsIgnoreCase("stats")) {
                new DacSign(e, SignType.STATS);
            }
            else {
                this.setNoValidSign(e, local);
            }
        }
        else if ((this.isPrefixInLine(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', e.getLine(1))).toLowerCase(), local) || this.isPrefixInLine(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', e.getLine(2))).toLowerCase(), local) || this.isPrefixInLine(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', e.getLine(3))).toLowerCase(), local)) && Permissions.hasPermission(e.getPlayer(), "dac.admin.makesigns", false)) {
            if (!Permissions.hasPermission(e.getPlayer(), "dac.admin.makesigns", false)) {
                this.setSignNoPermissions(e, local);
                return;
            }
            this.setNoValidSign(e, local);
        }
    }
    
    private boolean isPrefixInLine(final String line, final Language local) {
        return line.contains("[dac]") || line.contains(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.prefixLong.toLowerCase()))) || line.contains(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.prefixShort.toLowerCase())));
    }
    
    private void setSignNoPermissions(final SignChangeEvent e, final Language local) {
        e.setLine(0, ChatColor.translateAlternateColorCodes('&', local.signNoPermission0));
        e.setLine(1, ChatColor.translateAlternateColorCodes('&', local.signNoPermission1));
        e.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signNoPermission2));
        e.setLine(3, ChatColor.translateAlternateColorCodes('&', local.signNoPermission3));
    }
    
    private void setNoValidSign(final SignChangeEvent e, final Language local) {
        e.setLine(0, ChatColor.translateAlternateColorCodes('&', local.prefixLong.trim()));
        e.setLine(1, ChatColor.translateAlternateColorCodes('&', local.signNotValid1));
        e.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signNotValid2));
        e.setLine(3, ChatColor.translateAlternateColorCodes('&', local.signNotValid3));
    }
}
