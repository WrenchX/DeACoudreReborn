// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.PluginManager;
import me.poutineqc.deacoudre.events.ElytraToggle;
import me.poutineqc.deacoudre.events.PlayerInteract;
import me.poutineqc.deacoudre.events.BlockBreak;
import me.poutineqc.deacoudre.guis.ColorsGUI;
import me.poutineqc.deacoudre.events.AsyncPlayerChat;
import me.poutineqc.deacoudre.events.SignChange;
import me.poutineqc.deacoudre.events.PlayerTeleport;
import me.poutineqc.deacoudre.events.PlayerMove;
import me.poutineqc.deacoudre.events.PlayerDisconnect;
import org.bukkit.event.Listener;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.Bukkit;
import java.io.IOException;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandExecutor;
import me.poutineqc.deacoudre.instances.Arena;
import me.poutineqc.deacoudre.achievements.TopManager;
import me.poutineqc.deacoudre.commands.DacCommand;
import me.poutineqc.deacoudre.instances.User;
import net.milkbowl.vault.economy.Economy;
import me.poutineqc.deacoudre.commands.DacSign;
import me.poutineqc.deacoudre.commands.DaC;
import me.poutineqc.deacoudre.guis.JoinGUI;
import me.poutineqc.deacoudre.events.PlayerDamage;
import me.poutineqc.deacoudre.achievements.AchievementsGUI;
import me.poutineqc.deacoudre.guis.ChooseColorGUI;
import me.poutineqc.deacoudre.achievements.Achievement;
import org.bukkit.plugin.java.JavaPlugin;

public class DeACoudre extends JavaPlugin
{
    private Configuration config;
    private MySQL mysql;
    private Language mainLanguage;
    private PlayerData playerData;
    private ArenaData arenaData;
    private Achievement achievement;
    private ChooseColorGUI chooseColorGUI;
    private AchievementsGUI achievementsGUI;
    private PlayerDamage playerDamage;
    private JoinGUI joinGUI;
    private DaC dac;
    private DacSign signData;
    private static Economy econ;
    public static String NMS_VERSION;
    public static boolean aboveOneNine;
    
    public void onEnable() {
        final PluginDescriptionFile pdfFile = this.getDescription();
        final Logger logger = this.getLogger();
        DeACoudre.NMS_VERSION = this.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        DeACoudre.aboveOneNine = (DeACoudre.NMS_VERSION.startsWith("v1_9") || DeACoudre.NMS_VERSION.startsWith("v1_1") || DeACoudre.NMS_VERSION.startsWith("v2"));
        this.config = new Configuration(this);
        if (!this.initialiseEconomy()) {
            return;
        }
        new User(this);
        new Permissions(this);
        new DacCommand(this);
        this.loadLanguages();
        this.connectMySQL();
        this.playerData = new PlayerData(this);
        this.achievement = new Achievement(this);
        new TopManager(this);
        this.achievementsGUI = new AchievementsGUI(this);
        this.chooseColorGUI = new ChooseColorGUI(this);
        this.joinGUI = new JoinGUI(this);
        this.playerDamage = new PlayerDamage(this);
        this.arenaData = new ArenaData(this);
        this.signData = new DacSign(this);
        new Arena(this);
        this.registerEvents();
        this.getCommand("dac").setExecutor((CommandExecutor)this.dac);
        logger.info(String.valueOf(pdfFile.getName()) + " has been enabled (v" + pdfFile.getVersion() + ")");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
                Arena.loadArenas();
                DacSign.loadAllSigns();
            }
        }, 0L);
    }
    
    private void connectMySQL() {
        if (this.config.mysql) {
            this.mysql = new MySQL(this);
            if (this.mysql.hasConnection()) {
                this.createMySQLTables();
            }
        }
        else {
            this.mysql = new MySQL();
        }
    }
    
    private void createMySQLTables() {
        this.mysql.update("CREATE TABLE IF NOT EXISTS " + this.config.tablePrefix + "SIGNS (" + "uuid varchar(64), type varchar(32)," + "locationWorld varchar(32), locationX INT DEFAULT 0, locationY INT DEFAULT 0, locationZ INT DEFAULT 0);");
        this.mysql.update("ALTER TABLE " + this.config.tablePrefix + "SIGNS CONVERT TO CHARACTER SET utf8;");
        this.mysql.update("CREATE TABLE IF NOT EXISTS " + this.config.tablePrefix + "ARENAS (name varchar(32),world varchar(32)," + "minAmountPlayer INT DEFAULT 2, maxAmountPlayer INT DEFAULT 8, colorIndice LONG," + "minPointX INT DEFAULT 0,minPointY INT DEFAULT 0,minPointZ INT DEFAULT 0," + "maxPointX INT DEFAULT 0, maxPointY INT DEFAULT 0,maxPointZ INT DEFAULT 0," + "lobbyX DOUBLE DEFAULT 0,lobbyY DOUBLE DEFAULT 0,lobbyZ DOUBLE DEFAULT 0," + "lobbyPitch FLOAT DEFAULT 0,lobbyYaw FLOAT DEFAULT 0," + "plateformX DOUBLE DEFAULT 0,plateformY DOUBLE DEFAULT 0,plateformZ DOUBLE DEFAULT 0," + "plateformPitch FLOAT DEFAULT 0,plateformYaw FLOAT DEFAULT 0);");
        this.mysql.update("ALTER TABLE " + this.config.tablePrefix + "ARENAS CONVERT TO CHARACTER SET utf8;");
        this.mysql.update("CREATE TABLE IF NOT EXISTS " + this.config.tablePrefix + "PLAYERS (UUID varchar(64), name varchar(64), language varchar(32), timePlayed INT DEFAULT 0," + "money DOUBLE DEFAULT 0," + "gamesPlayed INT DEFAULT 0, gamesWon INT DEFAULT 0, gamesLost INT DEFAULT 0, DaCdone INT DEFAULT 0," + "completedArena BOOLEAN DEFAULT FALSE, 8playersGame BOOLEAN DEFAULT FALSE," + "reachRound100 BOOLEAN DEFAULT FALSE, DaCon42 BOOLEAN DEFAULT FALSE," + "colorRivalery BOOLEAN DEFAULT FALSE, longTime BOOLEAN DEFAULT FALSE);");
        this.mysql.update("ALTER TABLE " + this.config.tablePrefix + "PLAYERS CONVERT TO CHARACTER SET utf8;");
        this.mysql.update("CREATE OR REPLACE VIEW " + this.config.tablePrefix + "GAMESPLAYED AS SELECT name, gamesPlayed FROM " + this.config.tablePrefix + "PLAYERS ORDER BY gamesPlayed DESC LIMIT 10");
        this.mysql.update("CREATE OR REPLACE VIEW " + this.config.tablePrefix + "GAMESWON AS SELECT name, gamesWon FROM " + this.config.tablePrefix + "PLAYERS ORDER BY gamesWon DESC LIMIT 10");
        this.mysql.update("CREATE OR REPLACE VIEW " + this.config.tablePrefix + "GAMESLOST AS SELECT name, gamesLost FROM " + this.config.tablePrefix + "PLAYERS ORDER BY gamesLost DESC LIMIT 10");
        this.mysql.update("CREATE OR REPLACE VIEW " + this.config.tablePrefix + "DACDONE AS SELECT name, DaCdone FROM " + this.config.tablePrefix + "PLAYERS ORDER BY DaCdone DESC LIMIT 10");
    }
    
    public void loadLanguages() {
        Language.clearLanguages();
        new Language(this);
        new Language("en-US", false);
        new Language("fr-FR", false);
        new Language(this.config.language, false);
    }
    
    public void onDisable() {
        final PluginDescriptionFile pdfFile = this.getDescription();
        final Logger logger = this.getLogger();
        logger.info(String.valueOf(pdfFile.getName()) + " has been disabled.");
    }
    
    private void registerEvents() {
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents((Listener)this.playerData, (Plugin)this);
        pm.registerEvents((Listener)this.playerDamage, (Plugin)this);
        pm.registerEvents((Listener)new PlayerDisconnect(), (Plugin)this);
        pm.registerEvents((Listener)new PlayerMove(this), (Plugin)this);
        pm.registerEvents((Listener)new PlayerTeleport(this), (Plugin)this);
        pm.registerEvents((Listener)new SignChange(this, this.mainLanguage), (Plugin)this);
        pm.registerEvents((Listener)new AsyncPlayerChat(this), (Plugin)this);
        pm.registerEvents((Listener)this.chooseColorGUI, (Plugin)this);
        pm.registerEvents((Listener)this.achievementsGUI, (Plugin)this);
        pm.registerEvents((Listener)new ColorsGUI(this), (Plugin)this);
        pm.registerEvents((Listener)this.joinGUI, (Plugin)this);
        this.dac = new DaC(this);
        pm.registerEvents((Listener)new BlockBreak(), (Plugin)this);
        pm.registerEvents((Listener)new PlayerInteract(this, this.mainLanguage), (Plugin)this);
        if (DeACoudre.aboveOneNine) {
            pm.registerEvents((Listener)new ElytraToggle(), (Plugin)this);
        }
    }
    
    public boolean initialiseEconomy() {
        if (this.config.economyReward && !this.setupEconomy()) {
            this.getLogger().warning("Vault not found.");
            this.getLogger().warning("Add Vault to your plugins or disable monetary rewards in the config.");
            this.getLogger().info("Disabling DeACoudre...");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return false;
        }
        return true;
    }
    
    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (rsp == null) {
            return false;
        }
        DeACoudre.econ = (Economy)rsp.getProvider();
        return DeACoudre.econ != null;
    }
    
    public static boolean isEconomyEnabled() {
        return DeACoudre.econ != null;
    }
    
    public static Economy getEconomy() {
        return DeACoudre.econ;
    }
    
    public Configuration getConfiguration() {
        return this.config;
    }
    
    public PlayerData getPlayerData() {
        return this.playerData;
    }
    
    public ArenaData getArenaData() {
        return this.arenaData;
    }
    
    public Achievement getAchievement() {
        return this.achievement;
    }
    
    public MySQL getMySQL() {
        return this.mysql;
    }
    
    public ChooseColorGUI getChooseColorGUI() {
        return this.chooseColorGUI;
    }
    
    public AchievementsGUI getAchievementsGUI() {
        return this.achievementsGUI;
    }
    
    public PlayerDamage getPlayerDamage() {
        return this.playerDamage;
    }
    
    public JoinGUI getJoinGUI() {
        return this.joinGUI;
    }
    
    public DaC getDAC() {
        return this.dac;
    }
    
    public DacSign getSignData() {
        return this.signData;
    }
}
