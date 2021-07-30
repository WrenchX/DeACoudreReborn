// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {
    private FileConfiguration config;
    private File configFile;
    public List<ItemStack> rewardItems;
    public List<String> dispatchCommands;
    public boolean introInFrontOfEveryMessage;
    public String language;
    public boolean verbose;
    public boolean autostart;
    public int countdownTime;
    public int timeBeforePlayerTimeOut;
    public boolean timeOutKick;
    public int maxFailBeforeEnding;
    public boolean resetPoolBeforeGame;
    public boolean invisibleFlyingSpectators;
    public boolean broadcastStart;
    public boolean broadcastAchievements;
    public boolean broadcastCongradulations;
    public boolean economyReward;
    public boolean challengeReward;
    public double minAmountReward;
    public double maxAmountReward;
    public double bonusCompletingArena;
    public double challengeRewardFinishArenaFirstTime;
    public double challengeReward8PlayersGame;
    public double challengeRewardReachRound100;
    public double hiddenChallengeReward;
    public String itemReward;
    public boolean chatRooms;
    public boolean teleportAfterEnding;
    public boolean mysql;
    public String host;
    public int port;
    public String database;
    public String user;
    public String password;
    public String tablePrefix;

    public Configuration(final DeACoudre plugin) {
        this.rewardItems = new ArrayList<ItemStack>();
        this.dispatchCommands = new ArrayList<String>();
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!this.configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        this.loadConfig((Plugin) plugin);
    }

    public void loadConfig(final Plugin plugin) {
        this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.configFile);
        this.introInFrontOfEveryMessage = this.config.getBoolean("introInFrontOfEveryMessage", true);
        this.language = this.config.getString("language", "en-US");
        this.verbose = this.config.getBoolean("verbose", true);
        this.autostart = this.config.getBoolean("autostart", true);
        this.countdownTime = this.config.getInt("countdownTime", 60);
        this.timeBeforePlayerTimeOut = this.config.getInt("timeBeforePlayerTimeOut", 60);
        this.timeOutKick = this.config.getBoolean("timeOutKick", true);
        this.maxFailBeforeEnding = this.config.getInt("maxFailBeforeEnding", 10);
        this.chatRooms = this.config.getBoolean("chatRooms", false);
        this.invisibleFlyingSpectators = this.config.getBoolean("invisibleFlyingSpectators", true);
        this.teleportAfterEnding = this.config.getBoolean("teleportAfterEnding", true);
        this.resetPoolBeforeGame = this.config.getBoolean("resetPoolBeforeGame", true);
        this.mysql = this.config.getBoolean("mysql", false);
        this.host = this.config.getString("host", "localhost");
        this.port = this.config.getInt("port", 3306);
        this.database = this.config.getString("database", "minecraft");
        this.user = this.config.getString("user", "root");
        this.password = this.config.getString("password");
        this.tablePrefix = this.config.getString("tablePrefix", "deacoudre_");
        this.broadcastStart = this.config.getBoolean("enabledBroadcasts.broadcastStart", true);
        this.broadcastAchievements = this.config.getBoolean("enabledBroadcasts.broadcastAchievements", true);
        this.broadcastCongradulations = this.config.getBoolean("enabledBroadcasts.broadcastCongradulations", true);
        this.economyReward = this.config.getBoolean("economyReward", true);
        this.challengeReward = this.config.getBoolean("challengeReward", true);
        this.minAmountReward = this.config.getDouble("minAmountReward", 25.0);
        this.maxAmountReward = this.config.getDouble("maxAmountReward", 150.0);
        this.bonusCompletingArena = this.config.getDouble("bonusCompletingArena", 50.0);
        this.challengeRewardFinishArenaFirstTime = this.config.getDouble("challengeRewardFinishArenaFirstTime", 100.0);
        this.challengeReward8PlayersGame = this.config.getDouble("challengeReward8PlayersGame", 50.0);
        this.challengeRewardReachRound100 = this.config.getDouble("challengeRewardReachRound100", 75.0);
        this.hiddenChallengeReward = this.config.getDouble("hiddenChallengeReward", 100.0);
        this.itemReward = this.config.getString("itemReward", "none");
        this.dispatchCommands = (List<String>) this.config.getStringList("commands");
        if (!this.itemReward.equalsIgnoreCase("random") && !this.itemReward.equalsIgnoreCase("all")) {
            this.itemReward = "none";
        }
        if (!this.itemReward.equalsIgnoreCase("none")) {
            this.loadItemRewards(plugin);
        }
    }

    private Material getMaterial(final String type) {
        Material material = Material.STONE;
        try {
            final Material mat = Material.valueOf(type);

            if (mat != null)
                material = mat;

        } catch (final IllegalArgumentException ex) {
            throw new RuntimeException("An item " + type + " is not a valid Material!");
        }

        return material;
    }

    private void loadItemRewards(final Plugin plugin) {
        this.rewardItems.clear();
        for (final String items : this.config.getStringList("itemRewards")) {
            final String[] item = items.split(":");
            final Material material = this.getMaterial(item[0]);
            int amount = 1;
            String name = "-1";
            if (material == null) {
                plugin.getLogger().info("Error while trying to load the Item: " + items);
                plugin.getLogger().info("Item not found. Ignoring...");
            } else {
                try {
                    if (item.length > 1) {
                        amount = Integer.parseInt(item[1]);
                    }
                    if (amount > 64) {
                        plugin.getLogger().info("Error while trying to load the Item: " + items);
                        plugin.getLogger().info("Too much items. Ignoring..");
                        continue;
                    }
                    if (item.length > 2) {
                        name = item[2];
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().info("Error while trying to load the Item: " + items);
                    plugin.getLogger().info("Value not a number. Ignoring..");
                    continue;
                }
                final ItemStack tempReward = new ItemStack(material, amount);
                if (name != "-1") {
                    final ItemMeta tempMeta = tempReward.getItemMeta();
                    tempMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
                    tempReward.setItemMeta(tempMeta);
                }
                this.rewardItems.add(tempReward);
            }
        }
    }
}
