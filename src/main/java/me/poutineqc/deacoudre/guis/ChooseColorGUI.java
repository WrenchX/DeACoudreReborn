// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.guis;

import java.util.List;
import org.bukkit.inventory.Inventory;
import me.poutineqc.deacoudre.tools.ItemStackManager;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import me.poutineqc.deacoudre.tools.ColorManager;
import org.bukkit.inventory.ItemStack;
import me.poutineqc.deacoudre.Language;
import me.poutineqc.deacoudre.instances.GameState;
import me.poutineqc.deacoudre.instances.Arena;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.poutineqc.deacoudre.DeACoudre;
import me.poutineqc.deacoudre.PlayerData;
import org.bukkit.event.Listener;

public class ChooseColorGUI implements Listener
{
    private PlayerData playerData;
    
    public ChooseColorGUI(final DeACoudre plugin) {
        this.playerData = plugin.getPlayerData();
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getWhoClicked();
        final Language local = this.playerData.getLanguageOfPlayer(player);
        if (!ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.editColorGuiTitle)))) {
            return;
        }
        if (event.getAction() == InventoryAction.NOTHING || event.getAction() == InventoryAction.UNKNOWN) {
            return;
        }
        event.setCancelled(true);
        final ItemStack item = event.getCurrentItem();
        if (item.getType() != Material.STAINED_CLAY && item.getType() != Material.WOOL) {
            return;
        }
        final Arena arena = Arena.getArenaFromName(ChatColor.stripColor((String)event.getInventory().getItem(0).getItemMeta().getLore().get(0)));
        final ColorManager colorManager = arena.getColorManager();
        final boolean enchanted = item.getItemMeta().hasEnchants();
        if (enchanted && colorManager.getOnlyChoosenBlocks().size() <= arena.getMaxPlayer()) {
            local.sendMsg(player, local.editColorColorLessPlayer);
            this.openColorGUI(player, arena);
            return;
        }
        if (arena.getGameState() == GameState.UNREADY && (arena.getMinPoolPoint() == null || arena.getMaxPoolPoint() == null)) {
            player.closeInventory();
            local.sendMsg(player, local.editColorNoPool);
            return;
        }
        if (arena.getGameState() != GameState.READY && arena.getGameState() != GameState.UNREADY) {
            player.closeInventory();
            local.sendMsg(player, local.editColorActive);
            return;
        }
        if (arena.getColorManager().isBlockUsed(item)) {
            player.closeInventory();
            local.sendMsg(player, local.editColorChoosen);
            return;
        }
        int valueOfItem = item.getDurability();
        if (item.getType() == Material.STAINED_CLAY) {
            valueOfItem += 16;
        }
        if (enchanted) {
            colorManager.setColorIndice(arena.getColorManager().getColorIndice() - (int)Math.pow(2.0, valueOfItem));
        }
        else {
            colorManager.setColorIndice(arena.getColorManager().getColorIndice() + (int)Math.pow(2.0, valueOfItem));
        }
        arena.resetArena(item);
        this.openColorGUI(player, arena);
    }
    
    public void openColorGUI(final Player player, final Arena arena) {
        final Language local = this.playerData.getLanguageOfPlayer(player);
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', local.editColorGuiTitle));
        ItemStackManager icon = new ItemStackManager(Material.BOOKSHELF, 4);
        icon.setTitle(local.keyWordGuiInstrictions);
        String[] split;
        for (int length = (split = local.editColorGuiTooltip.split("\n")).length, k = 0; k < length; ++k) {
            final String loreLine = split[k];
            icon.addToLore(loreLine);
        }
        icon.addToInventory(inv);
        icon = new ItemStackManager(Material.STAINED_GLASS_PANE);
        icon.setData((short)10);
        icon.setTitle(" ");
        for (int i = 0; i < inv.getSize(); ++i) {
            switch (i) {
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 27:
                case 36:
                case 45: {
                    icon.setPosition(i);
                    icon.addToInventory(inv);
                    break;
                }
            }
        }
        final List<ItemStackManager> colorManager = arena.getColorManager().getAllBlocks();
        for (int j = 0; j < 32; ++j) {
            final ItemStackManager item = colorManager.get(j);
            item.setPosition((int)(Math.floor(j / 8.0) * 9.0 + 19.0 + j % 8));
            item.addToInventory(inv);
        }
        icon = new ItemStackManager(Material.PAPER);
        icon.setTitle("&eArena:");
        icon.addToLore("&f" + arena.getName());
        icon.setPosition(0);
        icon.addToInventory(inv);
        icon.setPosition(8);
        icon.addToInventory(inv);
        player.openInventory(inv);
    }
}
