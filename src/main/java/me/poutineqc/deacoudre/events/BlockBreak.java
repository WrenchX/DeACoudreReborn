// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.events;

import org.bukkit.event.EventHandler;
import me.poutineqc.deacoudre.commands.DacSign;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;

public class BlockBreak implements Listener
{
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Sign)) {
            return;
        }
        final Sign sign = (Sign)event.getBlock().getState();
        final DacSign dacSign = DacSign.getDacSign(sign.getLocation());
        if (dacSign != null) {
            DacSign.removeSign(dacSign);
        }
    }
}
