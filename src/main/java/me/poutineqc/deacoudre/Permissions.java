// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre;

import me.poutineqc.deacoudre.commands.DacCommand;
import org.bukkit.entity.Player;

public class Permissions
{
    private static DeACoudre plugin;
    public static final String PermissionMultiplier = "dacreward.multiplier.x";
    public static final String permissionMakeSigns = "dac.admin.makesigns";
    public static final String permissionAdvancedInfo = "dac.admin.info";
    
    public Permissions(final DeACoudre plugin) {
        Permissions.plugin = plugin;
    }
    
    public static boolean hasPermission(final Player player, final DacCommand command, final boolean warning) {
        return hasPermission(player, command.getPermission(), warning);
    }
    
    public static boolean hasPermission(final Player player, final String permission, final boolean warning) {
        if (player.hasPermission(permission)) {
            return true;
        }
        if (warning) {
            final Language local = Permissions.plugin.getPlayerData().getLanguageOfPlayer(player);
            local.sendMsg(player, local.errorNoPermission);
        }
        return false;
    }
}
