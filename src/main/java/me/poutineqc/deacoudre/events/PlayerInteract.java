// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.events;

import me.poutineqc.deacoudre.commands.SignType;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import me.poutineqc.deacoudre.commands.DacCommand;
import me.poutineqc.deacoudre.commands.DacSign;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import me.poutineqc.deacoudre.Language;
import me.poutineqc.deacoudre.DeACoudre;
import me.poutineqc.deacoudre.commands.DaC;
import me.poutineqc.deacoudre.achievements.AchievementsGUI;
import org.bukkit.event.Listener;

public class PlayerInteract implements Listener
{
    private AchievementsGUI achievementsGUI;
    private DaC dac;
    
    public PlayerInteract(final DeACoudre plugin, final Language local) {
        this.achievementsGUI = plugin.getAchievementsGUI();
        this.dac = plugin.getDAC();
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (!e.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!(e.getClickedBlock().getState() instanceof Sign)) {
            return;
        }
        final Sign s = (Sign)e.getClickedBlock().getState();
        final DacSign dacsign = DacSign.getDacSign(s.getLocation());
        if (dacsign == null) {
            return;
        }
        e.setCancelled(true);
        final Player player = e.getPlayer();
        switch (dacsign.getSignType()) {
            case COLOR: {
                this.dac.openColorGUI(DacCommand.getCommand("color"), player);
                break;
            }
            case JOIN: {
                this.dac.commandJoin(DacCommand.getCommand("join"), player, 2, s.getLine(2), true);
                break;
            }
            case PLAY: {
                this.dac.commandJoin(DacCommand.getCommand("join"), player, 2, s.getLine(2), false);
                break;
            }
            case QUIT: {
                this.dac.quitGame(DacCommand.getCommand("quit"), player);
                break;
            }
            case START: {
                this.dac.startGame(DacCommand.getCommand("start"), player);
                break;
            }
            case STATS: {
                this.achievementsGUI.openStats(player);
                break;
            }
        }
    }
}
