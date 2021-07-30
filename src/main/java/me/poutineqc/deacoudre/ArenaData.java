// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;

public class ArenaData
{
    private File arenaFile;
    private FileConfiguration arenaData;
    
    public ArenaData(final DeACoudre plugin) {
        this.arenaFile = new File(plugin.getDataFolder(), "arenaData.yml");
        if (!this.arenaFile.exists()) {
            try {
                this.arenaFile.createNewFile();
            }
            catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create arenaData.ylm.");
            }
        }
        this.loadArenaData();
    }
    
    public FileConfiguration getData() {
        return this.arenaData;
    }
    
    public void saveArenaData() {
        try {
            this.arenaData.save(this.arenaFile);
        }
        catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save arenaData.yml!");
        }
    }
    
    public void loadArenaData() {
        this.arenaData = (FileConfiguration)YamlConfiguration.loadConfiguration(this.arenaFile);
    }
}
