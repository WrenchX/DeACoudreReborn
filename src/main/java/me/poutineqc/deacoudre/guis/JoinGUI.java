// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.guis;

import java.util.Iterator;
import java.util.List;
import me.poutineqc.deacoudre.instances.GameState;
import me.poutineqc.deacoudre.tools.ItemStackManager;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import java.util.Collections;
import java.util.ArrayList;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import me.poutineqc.deacoudre.Language;
import org.bukkit.inventory.Inventory;
import me.poutineqc.deacoudre.instances.Arena;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.poutineqc.deacoudre.DeACoudre;
import me.poutineqc.deacoudre.PlayerData;
import org.bukkit.event.Listener;

public class JoinGUI implements Listener
{
    private PlayerData playerData;
    
    public JoinGUI(final DeACoudre plugin) {
        this.playerData = plugin.getPlayerData();
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Inventory inv = event.getInventory();
        final Player player = (Player)event.getWhoClicked();
        final Language local = this.playerData.getLanguageOfPlayer(player);
        if (!ChatColor.stripColor(inv.getTitle()).equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.joinGuiTitle)))) {
            return;
        }
        if (event.getAction().equals((Object)InventoryAction.NOTHING) || event.getAction().equals((Object)InventoryAction.UNKNOWN)) {
            return;
        }
        event.setCancelled(true);
        final ItemStack item = event.getCurrentItem();
        final String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordGuiNextPage)).equalsIgnoreCase(itemName)) {
            player.closeInventory();
            this.openJoinGui(player, Integer.parseInt(item.getItemMeta().getLore().get(0)));
            return;
        }
        if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordGuiPreviousPage)).equalsIgnoreCase(itemName)) {
            player.closeInventory();
            this.openJoinGui(player, Integer.parseInt(ChatColor.stripColor((String)item.getItemMeta().getLore().get(0))));
            return;
        }
        if (item.getType() != Material.INK_SACK) {
            return;
        }
        final Arena arena = Arena.getArenaFromName(itemName);
        if (arena == null) {
            return;
        }
        if (event.getAction() == InventoryAction.PICKUP_HALF) {
            player.closeInventory();
            arena.displayInformation(player);
            return;
        }
        arena.addPlayerToTeam(player, true);
        player.closeInventory();
    }
    
    public void openJoinGui(final Player player, final int page) {
        final Language local = this.playerData.getLanguageOfPlayer(player);
        final List<String> Arenas = new ArrayList<String>();
        for (final Arena arena : Arena.getArenas()) {
            Arenas.add(arena.getName());
        }
        Collections.sort(Arenas);
        for (int i = 0; i < (page - 1) * 36; ++i) {
            Arenas.remove(0);
        }
        int size;
        if (Arenas.size() > 36) {
            size = 54;
        }
        else {
            size = (int)(Math.ceil((Arenas.size() + 18.0) / 9.0) * 9.0);
        }
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, size, ChatColor.translateAlternateColorCodes('&', local.joinGuiTitle));
        ItemStackManager icon = new ItemStackManager(Material.BOOKSHELF, 4);
        icon.setTitle(ChatColor.translateAlternateColorCodes('&', local.keyWordGuiInstrictions));
        String[] split;
        for (int length = (split = local.joinGuiTooltip.split("\n")).length, k = 0; k < length; ++k) {
            final String s = split[k];
            icon.addToLore(s);
        }
        inv = icon.addToInventory(inv);
        if (page > 1) {
            icon = new ItemStackManager(Material.ARROW, 7);
            icon.setTitle(local.keyWordGuiPreviousPage);
            icon.addToLore(String.valueOf(page - 1));
            inv = icon.addToInventory(inv);
        }
        icon = new ItemStackManager(Material.STAINED_GLASS_PANE);
        icon.setTitle(" ");
        icon.setData((short)1);
        for (int j = 0; j < inv.getSize(); ++j) {
            switch (j) {
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17: {
                    icon.setPosition(j);
                    inv = icon.addToInventory(inv);
                    break;
                }
            }
        }
        icon = new ItemStackManager(Material.INK_SACK);
        int slot = 18;
        for (final String s2 : Arenas) {
            final Arena arena2 = Arena.getArenaFromName(s2);
            icon.clearLore();
            icon.setTitle(ChatColor.GOLD + s2);
            if (!arena2.isAllSet()) {
                icon.setData((short)8);
                icon.addToLore(ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateUnset));
            }
            else if (arena2.getGameState() == GameState.ACTIVE || arena2.getGameState() == GameState.ENDING) {
                icon.setData((short)12);
                icon.addToLore(ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateStarted));
            }
            else if (arena2.getUsers().size() >= arena2.getMaxPlayer()) {
                icon.setData((short)12);
                icon.addToLore(ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateFull));
                icon.addToLore(String.valueOf(ChatColor.translateAlternateColorCodes('&', local.keyWordScoreboardPlayers)) + ChatColor.DARK_GRAY + " : " + String.valueOf(arena2.getNonEliminated().size()) + "/" + String.valueOf(arena2.getMaxPlayer()));
            }
            else {
                icon.setData((short)10);
                icon.addToLore(ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateReady));
                icon.addToLore(String.valueOf(ChatColor.translateAlternateColorCodes('&', local.keyWordScoreboardPlayers)) + ChatColor.DARK_GRAY + " : " + String.valueOf(arena2.getNonEliminated().size()) + "/" + String.valueOf(arena2.getMaxPlayer()));
            }
            icon.setPosition(slot++);
            icon.addToInventory(inv);
            if (slot == 54 && Arenas.size() > 36) {
                icon = new ItemStackManager(Material.ARROW, 8);
                icon.setTitle(local.keyWordGuiNextPage);
                icon.addToLore(String.valueOf(page + 1));
                inv = icon.addToInventory(inv);
                break;
            }
        }
        player.openInventory(inv);
    }
}
