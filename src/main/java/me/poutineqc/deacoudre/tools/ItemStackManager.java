// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.tools;

import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import org.bukkit.Material;
import java.util.List;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;

public class ItemStackManager
{
    private int position;
    private ItemStack item;
    private ItemMeta meta;
    List<String> lore;
    
    public ItemStackManager(final Material material) {
        this.lore = new ArrayList<String>();
        this.item = new ItemStack(material);
        this.meta = this.item.getItemMeta();
    }
    
    public ItemStackManager(final Material material, final int position) {
        this.lore = new ArrayList<String>();
        this.position = position;
        this.item = new ItemStack(material);
        this.meta = this.item.getItemMeta();
    }
    
    public Material getMaterial() {
        return this.item.getType();
    }
    
    public short getData() {
        return this.item.getDurability();
    }
    
    public void setPosition(final int position) {
        this.position = position;
    }
    
    public void setData(final short data) {
        this.item.setDurability(data);
    }
    
    public void setTitle(final String displayName) {
        this.meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
    }
    
    public void addToLore(final String loreLine) {
        this.lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
    }
    
    public Inventory addToInventory(final Inventory inv) {
        this.meta.setLore((List)this.lore);
        this.item.setItemMeta(this.meta);
        inv.setItem(this.position, this.item);
        return inv;
    }
    
    public void addEnchantement(final Enchantment enchantment, final int level) {
        this.meta.addEnchant(Enchantment.DURABILITY, -1, true);
    }
    
    public ItemStack getItem() {
        this.meta.setLore((List)this.lore);
        this.item.setItemMeta(this.meta);
        return this.item;
    }
    
    public void setPlayerHeadName(final String player) {
        if (this.meta instanceof SkullMeta) {
            ((SkullMeta)this.meta).setOwner(player);
        }
    }
    
    public String getDisplayName() {
        return this.meta.getDisplayName();
    }
    
    public void clearLore() {
        this.lore.clear();
    }
    
    public void clearEnchantements() {
        if (this.meta.hasEnchant(Enchantment.DURABILITY)) {
            this.meta.removeEnchant(Enchantment.DURABILITY);
        }
    }
}
