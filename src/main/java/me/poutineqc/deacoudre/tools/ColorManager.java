// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.tools;

import me.poutineqc.deacoudre.Language;
import me.poutineqc.deacoudre.instances.User;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.Material;

import java.util.ArrayList;

import me.poutineqc.deacoudre.DeACoudre;
import me.poutineqc.deacoudre.ArenaData;
import me.poutineqc.deacoudre.instances.Arena;
import me.poutineqc.deacoudre.Configuration;
import me.poutineqc.deacoudre.MySQL;

import java.util.Arrays;
import java.util.List;

public class ColorManager {
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
        } else {
            this.arenaData.getData().set("arenas." + this.arena.getName() + ".colorIndice", (Object) colorIndice);
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
                icon = new ItemStackManager(terracottas.get(i % 16));
            } else {
                icon = new ItemStackManager(wools.get(i % 16));
            }

            final int value = (int) Math.pow(2.0, i);
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
        return availableBlocks.get((int) Math.floor(Math.random() * availableBlocks.size()));
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

    public List<ItemStackManager> getSpecificAvailableItems(final String material) {
        final List<ItemStackManager> specificAvailableBlocks = new ArrayList<ItemStackManager>();
        for (final ItemStackManager item : this.getAvailableBlocks()) {
            if (material.equals("WOOL")) {
                if (Tag.WOOL.isTagged(item.getItem().getType())) {
                    specificAvailableBlocks.add(item);
                }
            } else {
                if (item.getItem().toString().contains(Material.TERRACOTTA.toString())) {
                    specificAvailableBlocks.add(item);
                }
            }
        }
        return specificAvailableBlocks;
    }

    public boolean isBlockUsed(final ItemStack item) {
        for (final User user : this.arena.getUsers()) {
            if (user.getItemStack() != null && user.getItemStack().getType() == item.getType() && getData(user.getItemStack().getType()) == getData(item.getType())) {
                return true;
            }
        }
        return false;
    }

    public String getBlockMaterialName(final ItemStack item, final Language local) {
        if (Tag.WOOL.isTagged(item.getType())) {
            return local.keyWordColorWool;
        } else if (item.getType().toString().contains(Material.TERRACOTTA.toString())) {
            return local.keyWordColorClay;
        } else {
            return local.keyWordColorRandom;
        }
    }

    @Deprecated
    public String getBlockColorName(final ItemStack item, final Language local) {

        switch (getData(item.getType())) {
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

    public static int getData(final Material mat) {

        switch (mat) {
            case WHITE_WOOL:
            case WHITE_TERRACOTTA:
            case WHITE_STAINED_GLASS:
                return 0;
            case ORANGE_WOOL:
            case ORANGE_TERRACOTTA:
            case ORANGE_STAINED_GLASS:
                return 1;
            case MAGENTA_WOOL:
            case MAGENTA_TERRACOTTA:
            case MAGENTA_STAINED_GLASS:
                return 2;
            case LIGHT_BLUE_WOOL:
            case LIGHT_BLUE_TERRACOTTA:
            case LIGHT_BLUE_STAINED_GLASS:
                return 3;
            case YELLOW_WOOL:
            case YELLOW_TERRACOTTA:
            case YELLOW_STAINED_GLASS:
                return 4;
            case LIME_WOOL:
            case LIME_TERRACOTTA:
            case LIME_STAINED_GLASS:
                return 5;
            case PINK_WOOL:
            case PINK_TERRACOTTA:
            case PINK_STAINED_GLASS:
                return 6;
            case GRAY_WOOL:
            case GRAY_TERRACOTTA:
            case GRAY_STAINED_GLASS:
                return 7;
            case LIGHT_GRAY_WOOL:
            case LIGHT_GRAY_TERRACOTTA:
            case LIGHT_GRAY_STAINED_GLASS:
                return 8;
            case CYAN_WOOL:
            case CYAN_TERRACOTTA:
                return 9;
            case PURPLE_WOOL:
            case PURPLE_TERRACOTTA:
                return 10;
            case BLUE_WOOL:
            case BLUE_TERRACOTTA:
                return 11;
            case BROWN_WOOL:
            case BROWN_TERRACOTTA:
                return 12;
            case GREEN_WOOL:
            case GREEN_TERRACOTTA:
                return 13;
            case RED_WOOL:
            case RED_TERRACOTTA:
                return 14;
            case BLACK_WOOL:
            case BLACK_TERRACOTTA:
                return 15;
            default: {
                return 0;
            }
        }
    }

    public static List<Material> wools = Arrays.asList(Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.MAGENTA_WOOL,
            Material.LIGHT_BLUE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL, Material.PINK_WOOL, Material.GRAY_WOOL,
            Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL, Material.BROWN_WOOL,
            Material.GREEN_WOOL, Material.RED_WOOL, Material.BLACK_WOOL);

    public static List<Material> terracottas = Arrays.asList(Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA,
            Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA,
            Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA,
            Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA);

}
