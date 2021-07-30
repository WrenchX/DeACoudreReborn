// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre;

import java.util.Map;
import me.poutineqc.deacoudre.instances.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import java.sql.ResultSet;
import java.util.UUID;
import java.sql.SQLException;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import org.bukkit.event.Listener;

public class PlayerData implements Listener
{
    private File playerFile;
    private FileConfiguration playerData;
    private MySQL mysql;
    private Configuration config;
    private boolean lastVersion;
    private String latestVersion;
    HashMap<String, String> originalPlayerName;
    
    public PlayerData(final DeACoudre plugin) {
        this.originalPlayerName = new HashMap<String, String>();
        this.config = plugin.getConfiguration();
        this.mysql = plugin.getMySQL();
        this.playerFile = new File(plugin.getDataFolder(), "playerData.yml");
        if (!this.playerFile.exists()) {
            try {
                this.playerFile.createNewFile();
            }
            catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create playerData.ylm.");
            }
        }
        this.loadPlayerData();
    }
    
    public FileConfiguration getData() {
        return this.playerData;
    }
    
    public void savePlayerData() {
        try {
            this.playerData.save(this.playerFile);
        }
        catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save arenaData.yml!");
        }
    }
    
    public void loadPlayerData() {
        this.playerData = (FileConfiguration)YamlConfiguration.loadConfiguration(this.playerFile);
        this.generateOriginalPlayerNames();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.addOnFileIfNotExist(player);
        }
    }
    
    private void generateOriginalPlayerNames() {
        this.originalPlayerName.clear();
        if (this.mysql.hasConnection()) {
            final ResultSet query = this.mysql.query("SELECT UUID, name FROM " + this.config.tablePrefix + "PLAYERS");
            try {
                while (query.next()) {
                    this.originalPlayerName.put(query.getString("UUID"), query.getString("name"));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (this.playerData.contains("players")) {
            for (final String uuid : this.playerData.getConfigurationSection("players").getKeys(false)) {
                this.originalPlayerName.put(uuid, this.playerData.getString("players." + uuid + ".name", UUID.randomUUID().toString()));
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.addOnFileIfNotExist(player);
    }
    
    public void addOnFileIfNotExist(final Player player) {
        final String uuid = player.getUniqueId().toString();
        final boolean alreadyInData = this.originalPlayerName.containsKey(uuid);
        if (alreadyInData) {
            if (player.getName().equalsIgnoreCase(this.originalPlayerName.get(uuid))) {
                return;
            }
            if (this.mysql.hasConnection()) {
                this.mysql.update("UPDATE " + this.config.tablePrefix + "PLAYERS SET name='" + player.getName() + "' WHERE UUID='" + uuid + "';");
            }
            else {
                this.playerData.set("players." + uuid + ".name", (Object)player.getName());
                this.savePlayerData();
            }
        }
        else {
            this.originalPlayerName.put(uuid, player.getName());
            if (this.mysql.hasConnection()) {
                this.mysql.update("INSERT INTO " + this.config.tablePrefix + "PLAYERS (UUID, name) VALUES ('" + uuid + "','" + player.getName() + "');");
            }
            else {
                this.playerData.set("players." + uuid + ".name", (Object)player.getName());
                this.playerData.set("players." + uuid + ".language", (Object)"default");
                this.playerData.set("players." + uuid + ".gamesPlayed", (Object)0);
                this.playerData.set("players." + uuid + ".gamesWon", (Object)0);
                this.playerData.set("players." + uuid + ".gamesLost", (Object)0);
                this.playerData.set("players." + uuid + ".DaCdone", (Object)0);
                this.playerData.set("players." + uuid + ".stats.timePlayed", (Object)0);
                this.playerData.set("players." + uuid + ".stats.moneyGains", (Object)0);
                this.playerData.set("players." + uuid + ".challenges.completedArena", (Object)false);
                this.playerData.set("players." + uuid + ".challenges.8playersGame", (Object)false);
                this.playerData.set("players." + uuid + ".challenges.reachRound100", (Object)false);
                this.playerData.set("players." + uuid + ".challenges.DaCon42", (Object)false);
                this.playerData.set("players." + uuid + ".challenges.colorRivalery", (Object)false);
                this.playerData.set("players." + uuid + ".challenges.longTime", (Object)false);
                this.savePlayerData();
            }
        }
    }
    
    public Language getLanguageOfPlayer(final User user) {
        return this.getLanguageOfPlayer(user.getPlayer());
    }
    
    public Language getLanguageOfPlayer(final Player player) {
        String fileName = null;
        if (this.mysql.hasConnection()) {
            try {
                final ResultSet query = this.mysql.query("SELECT language FROM " + this.config.tablePrefix + "PLAYERS WHERE UUID='" + player.getUniqueId() + "';");
                if (query.next()) {
                    fileName = query.getString("language");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            fileName = this.playerData.getString("players." + player.getUniqueId().toString() + ".language", (String)null);
        }
        if (fileName == null) {
            return Language.getLanguages().get(this.config.language);
        }
        return this.getLanguage(fileName);
    }
    
    public Language getLanguage(final String fileName) {
        for (final Map.Entry<String, Language> local : Language.getLanguages().entrySet()) {
            if (local.getKey().equalsIgnoreCase(fileName)) {
                return local.getValue();
            }
        }
        if (Language.getLanguages().containsKey(this.config.language)) {
            return Language.getLanguages().get(this.config.language);
        }
        return Language.getLanguages().get("en-US");
    }
    
    public void setLanguage(final Player player, final String key) {
        if (this.mysql.hasConnection()) {
            this.mysql.update("UPDATE " + this.config.tablePrefix + "PLAYERS SET language='" + key + "' WHERE UUID='" + player.getUniqueId().toString() + "';");
        }
        else {
            this.playerData.set("players." + player.getUniqueId().toString() + ".language", (Object)key);
            this.savePlayerData();
        }
    }
    
    public boolean isLatestVersion() {
        return this.lastVersion;
    }
    
    public String getLatestVersion() {
        return this.latestVersion;
    }
}
