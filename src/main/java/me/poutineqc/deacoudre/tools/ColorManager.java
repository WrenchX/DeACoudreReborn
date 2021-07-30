// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.tools;

import me.poutineqc.deacoudre.Language;
import me.poutineqc.deacoudre.instances.User;
import org.bukkit.inventory.ItemStack;
import java.util.Iterator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Material;
import java.util.ArrayList;
import me.poutineqc.deacoudre.DeACoudre;
import me.poutineqc.deacoudre.ArenaData;
import me.poutineqc.deacoudre.instances.Arena;
import me.poutineqc.deacoudre.Configuration;
import me.poutineqc.deacoudre.MySQL;
import java.util.List;

public class ColorManager
{
    private long colorIndice;
    private List<ItemStackManager> allBlocks;
    private List<ItemStackManager> onlyChoosenBlocks;
    private MySQL mysql;
    private Configuration config;
    private Arena arena;
    private ArenaData arenaData;
    
    public ColorManager(final Long colorIndice, final DeACoudre plugin, final Arena arena) {
        this.colorIndice = colorIndice;
        this.mysql = plugin.getMySQL();
        this.arenaData = plugin.getArenaData();
        this.config = plugin.getConfiguration();
        this.arena = arena;
        this.updateLists();
    }
    
    public void setColorIndice(final long colorIndice) {
        this.colorIndice = colorIndice;
        this.updateLists();
        if (this.mysql.hasConnection()) {
            this.mysql.update("UPDATE " + this.config.tablePrefix + "ARENAS SET colorIndice=" + colorIndice + " WHERE name='" + this.arena.getName() + "';");
        }
        else {
            this.arenaData.getData().set("arenas." + this.arena.getName() + ".colorIndice", (Object)colorIndice);
            this.arenaData.saveArenaData();
        }
    }
    
    public void updateLists() {
        this.allBlocks = new ArrayList<ItemStackManager>();
        this.onlyChoosenBlocks = new ArrayList<ItemStackManager>();
        long tempColorIndice = this.colorIndice;
        for (int i = 31; i >= 0; --i) {
            ItemStackManager icon;
            if (i >= 16) {
                icon = new ItemStackManager(Material.STAINED_CLAY);
            }
            else {
                icon = new ItemStackManager(Material.WOOL);
            }
            icon.setData((short)(i % 16));
            final int value = (int)Math.pow(2.0, i);
            if (value <= tempColorIndice) {
                icon.addEnchantement(Enchantment.DURABILITY, 1);
                tempColorIndice -= value;
                this.onlyChoosenBlocks.add(0, icon);
            }
            this.allBlocks.add(0, icon);
        }
        if (this.onlyChoosenBlocks.size() == 0) {
            this.onlyChoosenBlocks = this.allBlocks;
        }
    }
    
    public ItemStackManager getRandomAvailableBlock() {
        final List<ItemStackManager> availableBlocks = this.getAvailableBlocks();
        return availableBlocks.get((int)Math.floor(Math.random() * availableBlocks.size()));
    }
    
    public List<ItemStackManager> getAvailableBlocks() {
        final List<ItemStackManager> availableBlocks = new ArrayList<ItemStackManager>();
        for (final ItemStackManager item : this.onlyChoosenBlocks) {
            if (this.isBlockUsed(item.getItem())) {
                continue;
            }
            availableBlocks.add(item);
        }
        return availableBlocks;
    }
    
    public List<ItemStackManager> getAllBlocks() {
        return this.allBlocks;
    }
    
    public List<ItemStackManager> getOnlyChoosenBlocks() {
        return this.onlyChoosenBlocks;
    }
    
    public long getColorIndice() {
        return this.colorIndice;
    }
    
    public List<ItemStackManager> getSpecificAvailableItems(final Material material) {
        final List<ItemStackManager> specificAvailableBlocks = new ArrayList<ItemStackManager>();
        for (final ItemStackManager item : this.getAvailableBlocks()) {
            if (item.getMaterial() == material) {
                specificAvailableBlocks.add(item);
            }
        }
        return specificAvailableBlocks;
    }
    
    public boolean isBlockUsed(final ItemStack item) {
        for (final User user : this.arena.getUsers()) {
            if (user.getItemStack() != null && user.getItemStack().getType() == item.getType() && user.getItemStack().getDurability() == item.getDurability()) {
                return true;
            }
        }
        return false;
    }
    
    public String getBlockMaterialName(final ItemStack item, final Language local) {
        switch (item.getType()) {
            case STAINED_CLAY: {
                return local.keyWordColorClay;
            }
            case WOOL: {
                return local.keyWordColorWool;
            }
            default: {
                return local.keyWordColorRandom;
            }
        }
    }
    
    public String getBlockColorName(final ItemStack item, final Language local) {
        switch (item.getDurability()) {
            case 0: {
                return local.keyWordColorWhite;
            }
            case 1: {
                return local.keyWordColorOrange;
            }
            case 2: {
                return local.keyWordColorMagenta;
            }
            case 3: {
                return local.keyWordColorLightBlue;
            }
            case 4: {
                return local.keyWordColorYellow;
            }
            case 5: {
                return local.keyWordColorLime;
            }
            case 6: {
                return local.keyWordColorPink;
            }
            case 7: {
                return local.keyWordColorGrey;
            }
            case 8: {
                return local.keyWordColorLightGrey;
            }
            case 9: {
                return local.keyWordColorCyan;
            }
            case 10: {
                return local.keyWordColorPurple;
            }
            case 11: {
                return local.keyWordColorBlue;
            }
            case 12: {
                return local.keyWordColorBrown;
            }
            case 13: {
                return local.keyWordColorGreen;
            }
            case 14: {
                return local.keyWordColorRed;
            }
            case 15: {
                return local.keyWordColorBlack;
            }
            default: {
                return local.keyWordColorRandom;
            }
        }
    }
}
