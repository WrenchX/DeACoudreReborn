// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.instances;

import org.bukkit.*;
import me.poutineqc.deacoudre.tools.ItemStackManager;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import me.poutineqc.deacoudre.tools.Utils;
import me.poutineqc.deacoudre.tools.JsonBuilder;

import java.util.Random;
import me.poutineqc.deacoudre.Permissions;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.util.Vector;
import me.poutineqc.deacoudre.commands.DacSign;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import me.poutineqc.deacoudre.Language;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Objective;
import java.util.List;
import me.poutineqc.deacoudre.tools.ColorManager;
import me.poutineqc.deacoudre.events.PlayerDamage;
import me.poutineqc.deacoudre.achievements.Achievement;
import me.poutineqc.deacoudre.ArenaData;
import me.poutineqc.deacoudre.PlayerData;
import me.poutineqc.deacoudre.MySQL;
import me.poutineqc.deacoudre.Configuration;
import me.poutineqc.deacoudre.DeACoudre;

public class Arena
{
    private static DeACoudre plugin;
    private static Configuration config;
    private static MySQL mysql;
    private static PlayerData playerData;
    private static ArenaData arenaData;
    private static Achievement achievements;
    protected static PlayerDamage playerDamage;
    private String name;
    private World world;
    private Location lobby;
    private Location plateform;
    private Location minPoint;
    private Location maxPoint;
    private int maxAmountPlayer;
    private int minAmountPlayer;
    private ColorManager colorManager;
    private List<User> users;
    private User activePlayer;
    private boolean someoneLostFinal;
    private int stallingAmount;
    private int roundNo;
    private int currentTile;
    private int totalTile;
    private long startTime;
    private boolean forceStart;
    private GameState gameState;
    private Objective objective;
    private Scoreboard scoreboard;
    private Team spectator;
    private static List<Arena> arenas;
    
    static {
        Arena.arenas = new ArrayList<Arena>();
    }
    
    public User getActivePlayer() {
        return this.activePlayer;
    }
    
    public Arena(final DeACoudre plugin) {
        this.users = new ArrayList<User>();
        this.someoneLostFinal = false;
        this.stallingAmount = 0;
        this.roundNo = 0;
        this.currentTile = 0;
        this.gameState = GameState.UNREADY;
        Arena.plugin = plugin;
        Arena.config = plugin.getConfiguration();
        Arena.mysql = plugin.getMySQL();
        Arena.arenaData = plugin.getArenaData();
        Arena.playerData = plugin.getPlayerData();
        Arena.achievements = plugin.getAchievement();
        Arena.playerDamage = plugin.getPlayerDamage();
    }
    
    public static void loadArenas() {
        Arena.arenas = new ArrayList<Arena>();
        if (Arena.mysql.hasConnection()) {
            try {
                final ResultSet arenas = Arena.mysql.query("SELECT * FROM " + Arena.config.tablePrefix + "ARENAS;");
                while (arenas.next()) {
                    final String name = arenas.getString("name");
                    final Long colorIndice = arenas.getLong("colorIndice");
                    final World world = Bukkit.getWorld(arenas.getString("world"));
                    final Location minPoint = new Location(world, (double)arenas.getInt("minPointX"), (double)arenas.getInt("minPointY"), (double)arenas.getInt("minPointZ"));
                    final Location maxPoint = new Location(world, (double)arenas.getInt("maxPointX"), (double)arenas.getInt("maxPointY"), (double)arenas.getInt("maxPointZ"));
                    final Location lobby = new Location(world, arenas.getDouble("lobbyX"), arenas.getDouble("lobbyY"), arenas.getDouble("lobbyZ"));
                    lobby.setPitch(arenas.getFloat("lobbyPitch"));
                    lobby.setYaw(arenas.getFloat("lobbyYaw"));
                    final Location plateform = new Location(world, arenas.getDouble("plateformX"), arenas.getDouble("plateformY"), arenas.getDouble("plateformZ"));
                    plateform.setPitch(arenas.getFloat("plateformPitch"));
                    plateform.setYaw(arenas.getFloat("plateformYaw"));
                    final int minAmountPlayer = arenas.getInt("minAmountPlayer");
                    final int maxAmountPlayer = arenas.getInt("maxAmountPlayer");
                    new Arena(name, world, minPoint, maxPoint, lobby, plateform, minAmountPlayer, maxAmountPlayer, colorIndice);
                }
            }
            catch (SQLException e) {
                Arena.plugin.getLogger().info("[MySQL] Error while loading arenas.");
            }
        }
        else {
            if (!Arena.arenaData.getData().contains("arenas")) {
                return;
            }
            for (final String arenaName : Arena.arenaData.getData().getConfigurationSection("arenas").getKeys(false)) {
                final ConfigurationSection cs = Arena.arenaData.getData().getConfigurationSection("arenas." + arenaName);
                Arena.playerData.getData().set("arenas." + arenaName + ".material", (Object)null);
                Arena.playerData.savePlayerData();
                final World world2 = Bukkit.getWorld(cs.getString("world"));
                final Long colorIndice2 = cs.getLong("colorIndice", 2122219134L);
                final int minAmountPlayer2 = cs.getInt("minPlayer", 2);
                final int maxAmountPlayer2 = cs.getInt("maxPlayer", 8);
                ConfigurationSection ccs = cs.getConfigurationSection("waterPool.minimum");
                final Location minPoint2 = new Location(world2, (double)ccs.getInt("x", 0), (double)ccs.getInt("y", 0), (double)ccs.getInt("z", 0));
                ccs = cs.getConfigurationSection("waterPool.maximum");
                final Location maxPoint2 = new Location(world2, (double)ccs.getInt("x", 0), (double)ccs.getInt("y", 0), (double)ccs.getInt("z", 0));
                ccs = cs.getConfigurationSection("lobby");
                final Location lobby2 = new Location(world2, ccs.getDouble("x", 0.0), ccs.getDouble("y", 0.0), ccs.getDouble("z", 0.0));
                lobby2.setPitch((float)ccs.getDouble("pitch", 0.0));
                lobby2.setYaw((float)ccs.getDouble("yaw", 0.0));
                ccs = cs.getConfigurationSection("plateform");
                final Location plateform2 = new Location(world2, ccs.getDouble("x", 0.0), ccs.getDouble("y", 0.0), ccs.getDouble("z", 0.0));
                plateform2.setPitch((float)ccs.getDouble("pitch", 0.0));
                plateform2.setYaw((float)ccs.getDouble("yaw", 0.0));
                new Arena(arenaName, world2, minPoint2, maxPoint2, lobby2, plateform2, minAmountPlayer2, maxAmountPlayer2, colorIndice2);
            }
        }
    }
    
    public Arena(final String name, final Player player) {
        this.users = new ArrayList<User>();
        this.someoneLostFinal = false;
        this.stallingAmount = 0;
        this.roundNo = 0;
        this.currentTile = 0;
        this.gameState = GameState.UNREADY;
        this.name = name;
        this.world = player.getWorld();
        Arena.arenas.add(this);
        this.colorManager = new ColorManager(2122219134L, Arena.plugin, this);
        this.minAmountPlayer = 2;
        this.maxAmountPlayer = 8;
        final Language local = Arena.playerData.getLanguage(Arena.config.language);
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        (this.spectator = this.scoreboard.registerNewTeam("spectator")).setCanSeeFriendlyInvisibles(true);
        this.setNameTagVisibilityNever();
        (this.objective = this.scoreboard.registerNewObjective(name, "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', ChatColor.AQUA + name + " &f: " + local.keyWordScoreboardPlayers));
        this.objective.getScore(ChatColor.GOLD + "-------------------").setScore(1);
        this.objective.getScore(ChatColor.GOLD + local.keyWordGeneralMinimum + " = " + ChatColor.AQUA + String.valueOf(this.minAmountPlayer)).setScore(this.minAmountPlayer);
        this.objective.getScore(ChatColor.GOLD + local.keyWordGeneralMaximum + " = " + ChatColor.AQUA + String.valueOf(this.maxAmountPlayer)).setScore(this.maxAmountPlayer);
        if (Arena.mysql.hasConnection()) {
            Arena.mysql.update("INSERT INTO " + Arena.config.tablePrefix + "ARENAS (name, world, colorIndice) " + "VALUES ('" + name + "','" + this.world.getName() + "','" + 2122219134L + "');");
        }
        else {
            Arena.arenaData.getData().set("arenas." + name + ".world", (Object)this.world.getName());
            Arena.arenaData.getData().set("arenas." + name + ".colorIndice", (Object)2122219134L);
            Arena.arenaData.saveArenaData();
        }
    }
    
    private void setNullIfDefault() {
        if (0.0 == this.minPoint.getX() && 0.0 == this.minPoint.getY() && 0.0 == this.minPoint.getZ()) {
            this.minPoint = null;
        }
        if (0.0 == this.maxPoint.getX() && 0.0 == this.maxPoint.getY() && 0.0 == this.maxPoint.getZ()) {
            this.maxPoint = null;
        }
        if (0.0 == this.lobby.getX() && 0.0 == this.lobby.getY() && 0.0 == this.lobby.getZ()) {
            this.lobby = null;
        }
        if (0.0 == this.plateform.getX() && 0.0 == this.plateform.getY() && 0.0 == this.plateform.getZ()) {
            this.plateform = null;
        }
        if (this.isReady()) {
            this.gameState = GameState.READY;
        }
    }
    
    private boolean isReady() {
        return this.lobby != null && this.plateform != null && this.minPoint != null && this.maxPoint != null;
    }
    
    public Arena(final String name, final World world, final Location minPoint, final Location maxPoint, final Location lobby, final Location plateform, final int minAmountPlayer, final int maxAmountPlayer, final long colorIndice) {
        this.users = new ArrayList<User>();
        this.someoneLostFinal = false;
        this.stallingAmount = 0;
        this.roundNo = 0;
        this.currentTile = 0;
        this.gameState = GameState.UNREADY;
        this.name = name;
        try {
            world.getName();
            this.world = world;
            this.minPoint = minPoint;
            this.maxPoint = maxPoint;
            this.lobby = lobby;
            this.plateform = plateform;
            this.setNullIfDefault();
        }
        catch (NullPointerException e) {
            this.world = null;
            this.minPoint = null;
            this.maxPoint = null;
            this.lobby = null;
            this.plateform = null;
        }
        this.minAmountPlayer = minAmountPlayer;
        this.maxAmountPlayer = maxAmountPlayer;
        this.colorManager = new ColorManager(colorIndice, Arena.plugin, this);
        final Language local = Arena.playerData.getLanguage(Arena.config.language);
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        (this.spectator = this.scoreboard.registerNewTeam("spectator")).setCanSeeFriendlyInvisibles(true);
        this.setNameTagVisibilityNever();
        (this.objective = this.scoreboard.registerNewObjective(name, "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', ChatColor.AQUA + name + " &f: " + local.keyWordScoreboardPlayers));
        this.objective.getScore(ChatColor.GOLD + "-------------------").setScore(1);
        this.objective.getScore(ChatColor.GOLD + local.keyWordGeneralMinimum + " = " + ChatColor.AQUA + String.valueOf(minAmountPlayer)).setScore(minAmountPlayer);
        this.objective.getScore(ChatColor.GOLD + local.keyWordGeneralMaximum + " = " + ChatColor.AQUA + String.valueOf(maxAmountPlayer)).setScore(maxAmountPlayer);
        final Logger logger = Arena.plugin.getLogger();
        final boolean maxChanged = false;
        if (this.minAmountPlayer < 2) {
            logger.info("The min amount of players for the arena " + name + " can't be below 2.");
            logger.info("Using by default '2'.");
            this.minAmountPlayer = 2;
        }
        if (this.maxAmountPlayer > 12) {
            logger.info("The max amount of players for the arena " + name + " can't be above 12.");
            logger.info("Using by default 12.");
            this.maxAmountPlayer = 12;
        }
        if (this.maxAmountPlayer > this.colorManager.getAvailableBlocks().size() && !maxChanged) {
            logger.info("The max amount of players for the arena " + name + " can't be above the amount of available colors.");
            logger.info("Using by default " + this.colorManager.getAvailableBlocks().size() + ".");
            this.maxAmountPlayer = this.colorManager.getAvailableBlocks().size();
        }
        Arena.arenas.add(this);
    }
    
    private void setNameTagVisibilityNever() {
        if (DeACoudre.aboveOneNine) {
            try {
                final Object option = Class.forName("org.bukkit.scoreboard.Team$Option").getEnumConstants()[0];
                final Object optionStatus = Class.forName("org.bukkit.scoreboard.Team$OptionStatus").getEnumConstants()[1];
                final Object craftTeam = Class.forName("org.bukkit.craftbukkit." + DeACoudre.NMS_VERSION + ".scoreboard.CraftTeam").cast(this.spectator);
                final Method method = craftTeam.getClass().getMethod("setOption", Class.forName("org.bukkit.scoreboard.Team$Option"), Class.forName("org.bukkit.scoreboard.Team$OptionStatus"));
                method.setAccessible(true);
                method.invoke(craftTeam, option, optionStatus);
                method.setAccessible(false);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Object option = null;
                Object[] enumConstants;
                for (int length = (enumConstants = Class.forName("org.bukkit.scoreboard.NameTagVisibility").getEnumConstants()).length, i = 0; i < length; ++i) {
                    final Object optionElement = enumConstants[i];
                    if (optionElement.toString().equalsIgnoreCase("NEVER")) {
                        option = optionElement;
                        break;
                    }
                }
                final Object craftTeam2 = Class.forName("org.bukkit.craftbukkit." + DeACoudre.NMS_VERSION + ".scoreboard.CraftTeam").cast(this.spectator);
                final Method method2 = craftTeam2.getClass().getMethod("setNameTagVisibility", Class.forName("org.bukkit.scoreboard.NameTagVisibility"));
                method2.setAccessible(true);
                method2.invoke(craftTeam2, option);
                method2.setAccessible(false);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void deleteArena() {
        DacSign.arenaDelete(this);
        Arena.arenas.remove(this);
        if (Arena.mysql.hasConnection()) {
            Arena.mysql.update("DELETE FROM " + Arena.config.tablePrefix + "ARENAS WHERE name='" + this.name + "';");
        }
        else {
            Arena.arenaData.getData().set("arenas." + this.name, (Object)null);
            Arena.arenaData.saveArenaData();
        }
    }
    
    public void setLobby(final Player player) {
        this.gameState = GameState.UNREADY;
        try {
            if (!this.world.getName().equalsIgnoreCase(player.getWorld().getName())) {
                DacSign.removePlaySigns(this);
            }
        }
        catch (NullPointerException ex) {}
        this.world = player.getWorld();
        (this.lobby = player.getLocation()).add(new Vector(0.0, 0.5, 0.0));
        if (Arena.mysql.hasConnection()) {
            Arena.mysql.update("UPDATE " + Arena.config.tablePrefix + "ARENAS SET world='" + this.world.getName() + "',lobbyX='" + this.lobby.getX() + "',lobbyY='" + this.lobby.getY() + "',lobbyZ='" + this.lobby.getZ() + "',lobbyPitch='" + this.lobby.getPitch() + "',lobbyYaw='" + this.lobby.getYaw() + "' WHERE name='" + this.name + "';");
        }
        else {
            final ConfigurationSection cs = Arena.arenaData.getData().getConfigurationSection("arenas." + this.name);
            cs.set("world", (Object)this.world.getName());
            cs.set("lobby.x", (Object)this.lobby.getX());
            cs.set("lobby.y", (Object)this.lobby.getY());
            cs.set("lobby.z", (Object)this.lobby.getZ());
            cs.set("lobby.pitch", (Object)this.lobby.getPitch());
            cs.set("lobby.yaw", (Object)this.lobby.getYaw());
            Arena.arenaData.saveArenaData();
        }
        if (this.isReady()) {
            this.gameState = GameState.READY;
        }
    }
    
    public void setPlateform(final Player player) {
        this.gameState = GameState.UNREADY;
        if (!this.world.getName().equalsIgnoreCase(player.getWorld().getName())) {
            DacSign.removePlaySigns(this);
        }
        this.world = player.getWorld();
        (this.plateform = player.getLocation()).add(new Vector(0.0, 0.5, 0.0));
        if (Arena.mysql.hasConnection()) {
            Arena.mysql.update("UPDATE " + Arena.config.tablePrefix + "ARENAS SET world='" + this.world.getName() + "',plateformX='" + this.plateform.getX() + "',plateformY='" + this.plateform.getY() + "',plateformZ='" + this.plateform.getZ() + "',plateformPitch='" + this.plateform.getPitch() + "',plateformYaw='" + this.plateform.getYaw() + "' WHERE name='" + this.name + "';");
        }
        else {
            final ConfigurationSection cs = Arena.arenaData.getData().getConfigurationSection("arenas." + this.name);
            cs.set("world", (Object)this.world.getName());
            cs.set("plateform.x", (Object)this.plateform.getX());
            cs.set("plateform.y", (Object)this.plateform.getY());
            cs.set("plateform.z", (Object)this.plateform.getZ());
            cs.set("plateform.pitch", (Object)this.plateform.getPitch());
            cs.set("plateform.yaw", (Object)this.plateform.getYaw());
            Arena.arenaData.saveArenaData();
        }
        if (this.isReady()) {
            this.gameState = GameState.READY;
        }
    }
    
    public boolean setPool(final org.bukkit.entity.Player player) {

        final Selection s = DeACoudre.selections.get(player.getUniqueId());
// note: not necessarily the player's current world, see the concepts page
        if (s == null) {
            return false;
        }
        if(s.getWorld() == null) {
            player.sendMessage(ChatColor.RED + "Your selection is not valid!");
        }
        World selectionWorld = s.getWorld();
        this.gameState = GameState.UNREADY;
        this.world = s.getWorld();
        if (!this.world.getName().equalsIgnoreCase(player.getWorld().getName())) {
            DacSign.removePlaySigns(this);
        }
        this.minPoint = s.getMinimumPoint();
        this.maxPoint = s.getMaximumPoint();
        this.setTotalTile();
        if (Arena.mysql.hasConnection()) {
            Arena.mysql.update("UPDATE " + Arena.config.tablePrefix + "ARENAS SET world='" + this.world.getName() + "',minPointX='" + this.minPoint.getBlockX() + "',minPointY='" + this.minPoint.getBlockY() + "',minPointZ='" + this.minPoint.getBlockZ() + "',maxPointX='" + this.maxPoint.getBlockX() + "',maxPointY='" + this.maxPoint.getBlockY() + "',maxPointZ='" + this.maxPoint.getBlockZ() + "' WHERE name='" + this.name + "';");
        }
        else {
            final ConfigurationSection cs = Arena.arenaData.getData().getConfigurationSection("arenas." + this.name);
            cs.set("world", (Object)this.world.getName());
            cs.set("waterPool.minimum.x", (Object)this.minPoint.getBlockX());
            cs.set("waterPool.minimum.y", (Object)this.minPoint.getBlockY());
            cs.set("waterPool.minimum.z", (Object)this.minPoint.getBlockZ());
            cs.set("waterPool.maximum.x", (Object)this.maxPoint.getBlockX());
            cs.set("waterPool.maximum.y", (Object)this.maxPoint.getBlockY());
            cs.set("waterPool.maximum.z", (Object)this.maxPoint.getBlockZ());
            Arena.arenaData.saveArenaData();
        }
        if (this.isReady()) {
            this.gameState = GameState.READY;
        }
        return true;
    }
    
    public void setMaximum(final Player player, final String arg) {
        final Language local = Arena.playerData.getLanguageOfPlayer(player);
        if (this.gameState != GameState.READY && this.gameState != GameState.UNREADY) {
            local.sendMsg(player, local.editLimitGameActive);
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(arg);
        }
        catch (NumberFormatException e) {
            local.sendMsg(player, local.editLimitNaN);
            return;
        }
        if (amount < this.minAmountPlayer) {
            local.sendMsg(player, local.editLimitErrorMinMax);
            return;
        }
        if (amount > this.colorManager.getOnlyChoosenBlocks().size()) {
            local.sendMsg(player, local.editColorColorLessPlayer);
            return;
        }
        if (amount > 12) {
            local.sendMsg(player, local.editLimitMaxAboveMax);
            return;
        }
        this.scoreboard.resetScores(ChatColor.GOLD + local.keyWordGeneralMaximum + " = " + ChatColor.AQUA + String.valueOf(this.maxAmountPlayer));
        this.maxAmountPlayer = amount;
        this.objective.getScore(ChatColor.GOLD + local.keyWordGeneralMaximum + " = " + ChatColor.AQUA + String.valueOf(this.maxAmountPlayer)).setScore(3);
        if (Arena.mysql.hasConnection()) {
            Arena.mysql.update("UPDATE " + Arena.config.tablePrefix + "ARENAS SET maxAmountPlayer=" + amount + " WHERE name='" + this.name + "';");
        }
        else {
            Arena.arenaData.getData().set("arenas." + this.name + ".maxPlayer", (Object)amount);
            Arena.arenaData.saveArenaData();
        }
        local.sendMsg(player, local.editLimitMaxSuccess.replace("%amount%", String.valueOf(amount)).replace("%arenaName%", this.name));
    }
    
    public void setMinimum(final Player player, final String arg) {
        final Language local = Arena.playerData.getLanguageOfPlayer(player);
        if (this.gameState != GameState.READY && this.gameState != GameState.UNREADY) {
            local.sendMsg(player, local.editLimitGameActive);
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(arg);
        }
        catch (NumberFormatException e) {
            local.sendMsg(player, local.editLimitNaN);
            return;
        }
        if (amount < 2) {
            local.sendMsg(player, local.editLimitMinBelowMin);
            return;
        }
        if (amount > this.maxAmountPlayer) {
            local.sendMsg(player, local.editLimitErrorMinMax);
            return;
        }
        this.scoreboard.resetScores(ChatColor.GOLD + local.keyWordGeneralMinimum + " = " + ChatColor.AQUA + String.valueOf(this.minAmountPlayer));
        this.minAmountPlayer = amount;
        this.objective.getScore(ChatColor.GOLD + local.keyWordGeneralMinimum + " = " + ChatColor.AQUA + String.valueOf(this.minAmountPlayer)).setScore(2);
        if (Arena.mysql.hasConnection()) {
            Arena.mysql.update("UPDATE " + Arena.config.tablePrefix + "ARENAS SET minAmountPlayer=" + amount + " WHERE name='" + this.name + "';");
        }
        else {
            Arena.arenaData.getData().set("arenas." + this.name + ".minPlayer", (Object)amount);
            Arena.arenaData.saveArenaData();
        }
        local.sendMsg(player, local.editLimitMinSuccess.replace("%amount%", String.valueOf(amount)).replace("%arenaName%", this.name));
    }
    
    public boolean isAllSet() {
        return this.lobby != null && this.plateform != null && this.maxPoint != null && this.minPoint != null;
    }
    
    public void displayInformation(final Player player) {
        final Language local = Arena.playerData.getLanguageOfPlayer(player);
        String stringGameState = null;
        switch (this.gameState) {
            case ACTIVE: {
                stringGameState = local.keyWordGameStateActive;
                break;
            }
            case READY: {
                stringGameState = local.keyWordGameStateReady;
                break;
            }
            case STARTUP: {
                stringGameState = local.keyWordGameStateStartup;
                break;
            }
            default: {
                stringGameState = local.keyWordGameStateUnset;
                break;
            }
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m" + StringUtils.repeat(" ", 13) + "&r &3DeACoudre &b" + local.keyWordHelpInformation + " &3: &b" + this.name + " &8&m" + StringUtils.repeat(" ", 13)));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordHelpCurrent + " " + local.keyWordGameState + ": &7" + stringGameState));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordHelpCurrent + " " + local.keyWordHelpAmountPlayer + ": &7" + this.getNonEliminated().size()));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordGeneralMinimum + " " + local.keyWordHelpAmountPlayer + ": &7" + this.minAmountPlayer));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordGeneralMaximum + " " + local.keyWordHelpAmountPlayer + ": &7" + this.maxAmountPlayer));
        player.sendMessage("\n");
        if (!Permissions.hasPermission(player, "dac.admin.info", false)) {
            return;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m" + StringUtils.repeat(" ", 5) + "&r &3DeACoudre &b" + local.keyWordHelpAdvanced + " &3: &b" + this.name + " &8&m" + StringUtils.repeat(" ", 5)));
        if (this.world == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordHelpWorld + ": &7null"));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordHelpWorld + ": &7" + this.world.getName()));
        }
        if (this.lobby == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordHelpLobby + ": &7null"));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordHelpLobby + ": &7{" + (int)(this.lobby.getX() * 100.0) / 100.0 + ", " + (int)(this.lobby.getY() * 100.0) / 100.0 + ", " + (int)(this.lobby.getZ() * 100.0) / 100.0 + "}"));
        }
        if (this.plateform == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordHelpPlateform + ": &7null"));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordHelpPlateform + ": &7{" + (int)(this.plateform.getX() * 100.0) / 100.0 + ", " + (int)(this.plateform.getY() * 100.0) / 100.0 + ", " + (int)(this.plateform.getZ() * 100.0) / 100.0 + "}"));
        }
        if (this.minPoint == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordGeneralMinimum + local.keyWordHelpPool + ": &7null"));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordGeneralMinimum + local.keyWordHelpPool + ": &7{" + this.minPoint.getBlockX() + ", " + this.minPoint.getBlockY() + ", " + this.minPoint.getBlockZ() + "}"));
        }
        if (this.maxPoint == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordGeneralMaximum + local.keyWordHelpPool + ": &7null"));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordGeneralMaximum + local.keyWordHelpPool + ": &7{" + this.maxPoint.getBlockX() + ", " + this.maxPoint.getBlockY() + ", " + this.maxPoint.getBlockZ() + "}"));
        }
        player.sendMessage("\n");
    }
    
    public void addPlayerToTeam(final Player player, final boolean tpAuto) {
        final Language local = Arena.playerData.getLanguageOfPlayer(player);
        final Arena arena = getArenaFromPlayer(player);
        if (arena != null) {
            local.sendMsg(player, local.errorAlreadyInGame);
            return;
        }
        if (this.gameState == GameState.UNREADY) {
            local.sendMsg(player, local.joinStateUnset);
            return;
        }
        boolean eliminated = false;
        if (this.gameState == GameState.ACTIVE || this.gameState == GameState.ENDING) {
            local.sendMsg(player, local.joinStateStarted);
            local.sendMsg(player, local.joinAsSpectator);
            eliminated = true;
        }
        if (!eliminated && this.getNonEliminated().size() >= this.maxAmountPlayer) {
            local.sendMsg(player, local.joinStateFull);
            local.sendMsg(player, local.joinAsSpectator);
            eliminated = true;
        }
        final User user = new User(player, this, tpAuto, eliminated);
        this.users.add(user);
        if (player.getLocation().distance(this.lobby) > 1.0 && tpAuto) {
            local.sendMsg(player, ChatColor.RED + "Error: Could not teleport you to the lobby. Failed to join the game.");
            this.removeUserFromGame(user, false);
            return;
        }
        DacSign.updateSigns(this);
        if (!eliminated) {
            local.sendMsg(player, local.joinGamePlayer.replace("%arenaName%", this.name).replace("%amountInGame%", String.valueOf(this.getNonEliminated().size())));
            for (final User u : this.users) {
                if (u != user) {
                    final Language localInstance = Arena.playerData.getLanguageOfPlayer(u);
                    localInstance.sendMsg(u, localInstance.joinGameOthers.replace("%player%", user.getDisplayName()).replace("%amountInGame%", String.valueOf(this.getNonEliminated().size())));
                }
            }
        }
        else if (this.gameState == GameState.ACTIVE || this.gameState == GameState.ENDING) {
            user.maxStats(true);
        }
        if (this.getNonEliminated().size() >= this.minAmountPlayer && Arena.config.autostart && this.gameState == GameState.READY) {
            if (this.startTime + 30000L > System.currentTimeMillis()) {
                local.sendMsg(player, local.startAutoFail);
                return;
            }
            this.gameState = GameState.STARTUP;
            this.setStartTime();
            this.countdown(this, Arena.config.countdownTime * 20);
            if (Arena.plugin.getConfiguration().broadcastStart) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    final Language localInstance = Arena.playerData.getLanguageOfPlayer(p);
                    localInstance.sendMsg(p, localInstance.startBroadcast.replaceAll("%arena%", this.name).replace("%time%", String.valueOf(Arena.config.countdownTime).toString()));
                }
            }
        }
    }
    
    public boolean removePlayerFromGame(final Player player) {
        final Arena arena = getArenaFromPlayer(player);
        if (arena == null) {
            final Language local = Arena.playerData.getLanguageOfPlayer(player);
            local.sendMsg(player, local.errorNotInGame);
            return false;
        }
        this.removeUserFromGame(this.getUser(player), true);
        return true;
    }
    
    public void removeUserFromGame(final User user, final boolean messages) {
        final User newUser = (this.getNonEliminated().size() == this.maxAmountPlayer && this.getNonEliminated().size() < this.users.size()) ? this.getFirstWaitingPlayer() : null;
        if (!user.isEliminated()) {
            this.eliminateUser(user);
            if (messages) {
                final Language local = Arena.playerData.getLanguageOfPlayer(user);
                local.sendMsg(user, local.quitGamePlayer);
                for (final User u : this.getUsers()) {
                    if (user != u) {
                        final Language localTemp = Arena.playerData.getLanguageOfPlayer(u);
                        localTemp.sendMsg(u.getPlayer(), localTemp.quitGameOthers.replace("%player%", user.getDisplayName()));
                    }
                }
            }
        }
        this.users.remove(user);
        this.scoreboard.resetScores(user.getName());
        user.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        if ((this.gameState == GameState.READY || this.gameState == GameState.STARTUP) && newUser != null && newUser != null) {
            newUser.unEliminate(this);
            final Language local = Arena.playerData.getLanguageOfPlayer(newUser);
            local.sendMsg(newUser, local.joinNewPlacePlayer.replace("%leaver%", user.getDisplayName()));
            for (final User u : this.users) {
                if (u != newUser) {
                    final Language localTemp = Arena.playerData.getLanguageOfPlayer(u);
                    localTemp.sendMsg(u.getPlayer(), localTemp.joinNewPlaceOthers.replace("%player%", newUser.getDisplayName()).replace("%leaver%", user.getDisplayName()));
                }
            }
        }
        DacSign.updateSigns(this);
        user.maxStats(false);
        user.returnOriginalPlayerStats();
        if (this.gameState == GameState.STARTUP && this.getNonEliminated().size() < this.minAmountPlayer) {
            this.gameState = GameState.READY;
        }
        if (this.gameState != GameState.ACTIVE) {
            return;
        }
        if (this.isOver()) {
            try {
                this.activePlayer.getPlayer().teleport(this.lobby);
            }
            catch (NullPointerException ex) {}
            this.finishGame(false);
        }
        else if (user == this.activePlayer) {
            this.nextPlayer();
        }
    }
    
    private User getFirstWaitingPlayer() {
        for (final User user : this.users) {
            if (user.isEliminated()) {
                return user;
            }
        }
        return null;
    }
    
    private void eliminateUser(final User user) {
        user.eliminate();
        if (this.gameState != GameState.ACTIVE) {
            return;
        }
        int gameLost = 0;
        if (Arena.mysql.hasConnection()) {
            final ResultSet query = Arena.mysql.query("SELECT gamesLost FROM " + Arena.config.tablePrefix + "PLAYERS WHERE UUID='" + user.getUUID() + "';");
            try {
                if (query.next()) {
                    gameLost = query.getInt("gamesLost");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            Arena.mysql.update("UPDATE " + Arena.config.tablePrefix + "PLAYERS SET gamesLost='" + ++gameLost + "' WHERE UUID='" + user.getUUID() + "';");
        }
        else {
            gameLost = Arena.playerData.getData().getInt("players." + user.getUUID() + ".gamesLost", 0) + 1;
            Arena.playerData.getData().set("players." + user.getUUID() + ".gamesLost", (Object)gameLost);
            Arena.playerData.savePlayerData();
        }
        Arena.achievements.testAchievement(".gamesLost", user.getPlayer());
        this.updateStats(user);
    }
    
    private void updateStats(final User user) {
        int games = 0;
        int timePlayed = 0;
        if (Arena.mysql.hasConnection()) {
            final ResultSet query = Arena.mysql.query("SELECT gamesPlayed, timePlayed FROM " + Arena.config.tablePrefix + "PLAYERS WHERE UUID='" + user.getUUID() + "';");
            try {
                if (query.next()) {
                    games = query.getInt("gamesPlayed");
                    timePlayed = query.getInt("timePlayed");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            games = Arena.playerData.getData().getInt("players." + user.getUUID() + ".gamesPlayed", 0);
            timePlayed = Arena.playerData.getData().getInt("players." + user.getUUID() + ".stats.timePlayed", 0);
        }
        ++games;
        timePlayed += (int)(System.currentTimeMillis() - this.startTime);
        if (Arena.mysql.hasConnection()) {
            Arena.mysql.update("UPDATE " + Arena.config.tablePrefix + "PLAYERS SET timePlayed='" + timePlayed + "', gamesPlayed='" + games + "' WHERE UUID='" + user.getUUID() + "';");
        }
        else {
            Arena.playerData.getData().set("players." + user.getUUID() + ".stats.timePlayed", (Object)timePlayed);
            Arena.playerData.getData().set("players." + user.getUUID() + ".gamesPlayed", (Object)games);
            Arena.playerData.savePlayerData();
        }
        Arena.achievements.testAchievement(".gamesPlayed", user.getPlayer());
    }
    
    public void startGame(final boolean forceStart) {
        this.forceStart = forceStart;
        final Random r = new Random();
        final List<User> temporary = new ArrayList<User>(this.getNonEliminated());
        for (final User user : temporary) {
            final Player player = user.getPlayer();
            final Language local = Arena.playerData.getLanguageOfPlayer(player);
            if (user.getItemStack() == null) {
                user.setColor(this.colorManager.getRandomAvailableBlock().getItem());
                local.sendMsg(user.getPlayer(), local.startRandomColor.replace("%material%", this.colorManager.getBlockMaterialName(user.getItemStack(), local)).replace("%color%", this.colorManager.getBlockColorName(user.getItemStack(), local)));
            }
        }
        if (!forceStart) {
            for (final User user : this.users) {
                final Language local2 = Arena.playerData.getLanguageOfPlayer(user);
                local2.sendMsg(user, local2.startRandomOrder);
            }
        }
        int i = 1;
        while (!temporary.isEmpty()) {
            final int j = r.nextInt(temporary.size());
            final User user2 = temporary.get(j);
            user2.setPlace(i);
            temporary.remove(j);
            if (!forceStart) {
                for (final User u : this.users) {
                    final Language local3 = Arena.playerData.getLanguageOfPlayer(u);
                    local3.sendMsg(u, local3.startPosition.replace("%player%", user2.getDisplayName()).replace("%posNo%", String.valueOf(i)));
                }
            }
            ++i;
        }
        final Language local4 = Arena.playerData.getLanguage(Arena.config.language);
        this.objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', ChatColor.AQUA + this.name + " &f: " + local4.keyWordScoreboardPoints));
        this.objective.getScore(ChatColor.GOLD + "-------------------").setScore(98);
        this.objective.getScoreboard().resetScores(ChatColor.GOLD + local4.keyWordGeneralMinimum + " = " + ChatColor.AQUA + String.valueOf(this.minAmountPlayer));
        this.objective.getScoreboard().resetScores(ChatColor.GOLD + local4.keyWordGeneralMaximum + " = " + ChatColor.AQUA + String.valueOf(this.maxAmountPlayer));
        if (this.getNonEliminated().size() == 8) {
            for (final User user3 : this.getNonEliminated()) {
                Arena.achievements.testAchievement(".challenges.8playersGame", user3.getPlayer());
            }
        }
        this.gameState = GameState.ACTIVE;
        DacSign.updateSigns(this);
        this.startTime = System.currentTimeMillis();
        this.fillWater();
        this.setTotalTile();
        this.activePlayer = this.lastPlayer();
        this.nextPlayer();
    }
    
    public void nextPlayer() {
        if (this.isDacFinished()) {
            if (this.forceStart) {
                for (final User user : this.getNonEliminated()) {
                    user.eliminate();
                }
            }
            this.finishGame(true);
            return;
        }
        if (!this.forceStart && this.stallingAmount > Arena.config.maxFailBeforeEnding) {
            this.reviveConfirmationQueue();
            this.finishGame(false);
            return;
        }
        if (this.isLastPlayer(this.activePlayer)) {
            this.newRound();
            if (this.getRoundNo() == 100 && !this.forceStart) {
                for (final User user : this.getNonEliminated()) {
                    Arena.achievements.testAchievement(".challenges.reachRound100", user.getPlayer());
                }
            }
            this.setSomeoneLostFinal(false);
            for (final User user : this.users) {
                user.setRoundSuccess(false);
            }
        }
        this.activePlayer.getPlayer().setLevel(0);
        this.activePlayer.getPlayer().setExp(0.0f);
        this.activePlayer = new User(this.activePlayer.getPlace());
        final Arena arena = this;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)Arena.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                Arena.access$1(Arena.this, Arena.this.getNextPlayer());
                final Player player = Arena.this.activePlayer.getPlayer();
                Language local = null;
                try {
                    local = Arena.playerData.getLanguageOfPlayer(player);
                }
                catch (NullPointerException e) {
                    return;
                }
                if (Arena.config.verbose) {
                    local.sendMsg(Arena.this.activePlayer, local.gameTurnPlayer);
                    for (final User user : Arena.this.users) {
                        if (Arena.this.activePlayer != user) {
                            final Language localInstance = Arena.playerData.getLanguageOfPlayer(user);
                            localInstance.sendMsg(user, localInstance.gameTurnOthers.replace("%player%", Arena.this.activePlayer.getPlayer().getDisplayName()));
                        }
                    }
                }
                player.teleport(Arena.this.plateform);
                Arena.this.activePlayer.maxStats(false);
                Arena.this.scoreboard.resetScores(Arena.this.activePlayer.getName());
                Arena.this.objective.getScore(ChatColor.AQUA + Arena.this.activePlayer.getName()).setScore(Arena.this.activePlayer.getPoint());
                Arena.this.timeOut(Arena.this.activePlayer, arena, Arena.config.timeBeforePlayerTimeOut * 20, Arena.this.roundNo);
                Utils.sendTitle(player, JsonBuilder.getJson(new JsonBuilder.JsonElement(local.keyWordJumpFast, ChatColor.GOLD, true, false, false, false, false)), JsonBuilder.getEmpty(), 5, 10, 5);
            }
        }, 6L);
    }
    
    public void flushConfirmationQueue(final User user) {
        for (final User u : this.getNonEliminated()) {
            if (u.isWaitingForConfirmation() && u.getPoint() == -1) {
                this.eliminateUser(u);
                final Language local = Arena.playerData.getLanguageOfPlayer(u);
                local.sendMsg(u, local.gamePointsFlushPlayer.replace("%player%", user.getPlayer().getDisplayName()));
                for (final User op : this.users) {
                    if (op != u) {
                        final Language localInstance = Arena.playerData.getLanguageOfPlayer(op);
                        localInstance.sendMsg(op, localInstance.gamePointsFlushOthers.replace("%player%", user.getDisplayName()).replace("%looser%", u.getDisplayName()));
                    }
                }
            }
        }
    }
    
    public void reviveConfirmationQueue() {
        for (final User user : this.getNonEliminated()) {
            if (user.isWaitingForConfirmation()) {
                user.addPoint();
                final Language local = Arena.playerData.getLanguageOfPlayer(user);
                local.sendMsg(user, local.gamePointsRevivePlayer.replace("%points%", String.valueOf(user.getPoint())));
                for (final User u : this.users) {
                    if (u != user) {
                        final Language localInstance = Arena.playerData.getLanguageOfPlayer(u);
                        localInstance.sendMsg(u, localInstance.gamePointsReviveOthers.replace("%player%", user.getDisplayName()).replace("%points%", String.valueOf(user.getPoint())));
                    }
                }
            }
        }
    }
    
    public void finishGame(final boolean dacDone) {
        this.gameState = GameState.ENDING;
        double reward = 0.0;
        if (this.currentTile > 100) {
            this.currentTile = 100;
        }
        final List<User> nonEliminated = this.getNonEliminated();
        if (nonEliminated.size() > 0) {
            if (nonEliminated.size() == 1) {
                for (final Player p : this.getBroadcastCongratzList()) {
                    final Language localInstance = Arena.playerData.getLanguageOfPlayer(p);
                    localInstance.sendMsg(p, localInstance.endingBroadcastSingle.replace("%player%", nonEliminated.get(0).getPlayer().getDisplayName()).replace("%arenaName%", this.name).toString());
                }
                reward = this.currentTile * this.currentTile / 10000 * (Arena.config.maxAmountReward - Arena.config.minAmountReward) + Arena.config.minAmountReward;
            }
            else if (dacDone) {
                for (final User user : nonEliminated) {
                    Arena.achievements.testAchievement(".challenges.completedArena", user.getPlayer());
                }
                for (final Player player : this.getBroadcastCongratzList()) {
                    final Language localInstance = Arena.playerData.getLanguageOfPlayer(player);
                    localInstance.sendMsg(player, localInstance.endingBroadcastMultiple.replace("%players%", this.getPlayerListToDisplay(localInstance)).replace("%arenaName%", this.name).toString());
                }
                reward = this.currentTile * this.currentTile / 10000 * (Arena.config.maxAmountReward - Arena.config.minAmountReward) + Arena.config.minAmountReward + Arena.config.bonusCompletingArena;
            }
            else {
                for (final User user : this.users) {
                    final Language localInstance = Arena.playerData.getLanguageOfPlayer(user);
                    localInstance.sendMsg(user.getPlayer(), localInstance.endingStall.replace("%time%", String.valueOf(Arena.config.maxFailBeforeEnding)));
                }
                while (1 < nonEliminated.size()) {
                    if (nonEliminated.get(0).getPoint() <= nonEliminated.get(1).getPoint()) {
                        this.eliminateUser(nonEliminated.get(0));
                        nonEliminated.remove(0);
                    }
                    else {
                        this.eliminateUser(nonEliminated.get(1));
                        nonEliminated.remove(1);
                    }
                }
                for (final Player player : this.getBroadcastCongratzList()) {
                    final Language localInstance = Arena.playerData.getLanguageOfPlayer(player);
                    localInstance.sendMsg(player, localInstance.endingBroadcastSingle.replace("%player%", nonEliminated.get(0).getDisplayName()).replace("%arenaName%", this.name).toString());
                }
                reward = this.currentTile * this.currentTile / 10000 * (Arena.config.maxAmountReward - Arena.config.minAmountReward) + Arena.config.minAmountReward;
            }
            for (final String s : Arena.config.dispatchCommands) {
                if (!s.contains("%winner%")) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), s.replace("%arena%", this.name));
                }
            }
            for (final User user : nonEliminated) {
                final Player player2 = user.getPlayer();
                final String UUID = player2.getUniqueId().toString();
                int gamesWon = 0;
                if (Arena.mysql.hasConnection()) {
                    final ResultSet query = Arena.mysql.query("SELECT gamesWon FROM " + Arena.config.tablePrefix + "PLAYERS WHERE UUID='" + user.getUUID() + "';");
                    try {
                        if (query.next()) {
                            gamesWon = query.getInt("gamesWon");
                        }
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Arena.mysql.update("UPDATE " + Arena.config.tablePrefix + "PLAYERS SET gamesWon='" + ++gamesWon + "' WHERE UUID='" + user.getUUID() + "';");
                }
                else {
                    gamesWon = Arena.playerData.getData().getInt("players." + UUID + ".gamesWon") + 1;
                    Arena.playerData.getData().set("players." + UUID + ".gamesWon", (Object)gamesWon);
                    Arena.playerData.savePlayerData();
                }
                Arena.achievements.testAchievement(".gamesWon", user);
                this.updateStats(user);
                for (final String s2 : Arena.config.dispatchCommands) {
                    if (s2.contains("%winner%")) {
                        Bukkit.dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), s2.replace("%arena%", this.name).replace("%winner%", player2.getName()));
                    }
                }
                if (Arena.config.economyReward) {
                    final Language localInstance2 = Arena.playerData.getLanguageOfPlayer(player2);
                    double newReward = Math.floor(reward);
                    boolean op = false;
                    if (player2.isOp()) {
                        op = true;
                    }
                    player2.setOp(false);
                    for (short i = 255; i > 0; --i) {
                        if (player2.hasPermission("dacreward.multiplier.x".replace("x", String.valueOf(i)))) {
                            newReward = Math.floor(reward * (100 + i * 25) / 100.0);
                            break;
                        }
                    }
                    player2.setOp(op);
                    DeACoudre.getEconomy().depositPlayer((OfflinePlayer)user.getPlayer(), newReward);
                    localInstance2.sendMsg(user.getPlayer(), localInstance2.endingRewardMoney.replace("%amount%", String.valueOf(newReward)).replace("%currency%", DeACoudre.getEconomy().currencyNamePlural()));
                    double moneyGains = 0.0;
                    if (Arena.mysql.hasConnection()) {
                        final ResultSet query2 = Arena.mysql.query("SELECT money FROM " + Arena.config.tablePrefix + "PLAYERS WHERE UUID='" + user.getUUID() + "';");
                        try {
                            if (query2.next()) {
                                moneyGains = query2.getDouble("money");
                            }
                        }
                        catch (SQLException e2) {
                            e2.printStackTrace();
                        }
                        Arena.mysql.update("UPDATE " + Arena.config.tablePrefix + "PLAYERS SET money='" + (moneyGains + newReward) + "' WHERE UUID='" + user.getUUID() + "';");
                    }
                    else {
                        moneyGains = Arena.playerData.getData().getDouble("players." + UUID + ".stats.moneyGains") + newReward;
                        Arena.playerData.getData().set("players." + UUID + ".stats.moneyGains", (Object)moneyGains);
                        Arena.playerData.savePlayerData();
                    }
                }
                if (!Arena.config.itemReward.equalsIgnoreCase("none")) {
                    final Language localInstance2 = Arena.playerData.getLanguageOfPlayer(player2);
                    if (Arena.config.itemReward.equalsIgnoreCase("all")) {
                        if (player2.getInventory().firstEmpty() == -1) {
                            localInstance2.sendMsg(player2, localInstance2.endingRewardItemsSpaceOne);
                        }
                        else {
                            for (final ItemStack itemReward : Arena.config.rewardItems) {
                                if (player2.getInventory().firstEmpty() == -1) {
                                    localInstance2.sendMsg(player2, localInstance2.endingRewardItemsSpaceMultiple);
                                }
                                else {
                                    player2.getInventory().addItem(new ItemStack[] { itemReward });
                                    if (itemReward.getItemMeta().hasDisplayName()) {
                                        localInstance2.sendMsg(player2, localInstance2.endingRewardItemsReceive.replace("%amount%", String.valueOf(itemReward.getAmount())).replace("%item%", itemReward.getItemMeta().getDisplayName()));
                                    }
                                    else {
                                        localInstance2.sendMsg(player2, localInstance2.endingRewardItemsReceive.replace("%amount%", String.valueOf(itemReward.getAmount())).replace("%item%", itemReward.getType().name()));
                                    }
                                }
                            }
                        }
                    }
                    else if (player2.getInventory().firstEmpty() == -1) {
                        localInstance2.sendMsg(player2, localInstance2.endingRewardItemsSpaceOne);
                    }
                    else {
                        final ItemStack itemReward = Arena.config.rewardItems.get(new Random().nextInt(Arena.config.rewardItems.size()));
                        player2.getInventory().addItem(new ItemStack[] { itemReward });
                        if (itemReward.getItemMeta().hasDisplayName()) {
                            localInstance2.sendMsg(player2, localInstance2.endingRewardItemsReceive.replace("%amount%", String.valueOf(itemReward.getAmount())).replace("%item%", itemReward.getItemMeta().getDisplayName()));
                        }
                        else {
                            localInstance2.sendMsg(player2, localInstance2.endingRewardItemsReceive.replace("%amount%", String.valueOf(itemReward.getAmount())).replace("%item%", itemReward.getType().name()));
                        }
                    }
                }
            }
        }
        else if (this.forceStart) {
            for (final User user : this.users) {
                final Language local = Arena.playerData.getLanguageOfPlayer(user);
                local.sendMsg(user, local.endingSimulation);
            }
        }
        if (Arena.config.teleportAfterEnding) {
            for (final User u : this.users) {
                final Language local = Arena.playerData.getLanguageOfPlayer(u);
                local.sendMsg(u, local.endingTeleport);
            }
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)Arena.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                Arena.this.kickPlayers();
                if (!Arena.config.resetPoolBeforeGame) {
                    Arena.this.fillWater();
                }
            }
        }, Arena.config.teleportAfterEnding ? 100L : 0L);
    }
    
    private List<Player> getBroadcastCongratzList() {
        List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        if (!Arena.config.broadcastCongradulations) {
            players = new ArrayList<Player>();
            for (final User user : this.users) {
                players.add(user.getPlayer());
            }
        }
        return players;
    }
    
    private void kickPlayers() {
        for (final User user : this.users) {
            user.maxStats(false);
            user.returnOriginalPlayerStats();
        }
        final Language local = Arena.playerData.getLanguage(Arena.config.language);
        this.spectator.unregister();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        (this.spectator = this.scoreboard.registerNewTeam("spectator")).setCanSeeFriendlyInvisibles(true);
        this.setNameTagVisibilityNever();
        (this.objective = this.scoreboard.registerNewObjective(this.name, "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', ChatColor.AQUA + this.name + " &f: " + local.keyWordScoreboardPlayers));
        this.objective.getScore(ChatColor.GOLD + "-------------------").setScore(1);
        this.objective.getScore(ChatColor.GOLD + local.keyWordGeneralMinimum + " = " + ChatColor.AQUA + String.valueOf(this.minAmountPlayer)).setScore(2);
        this.objective.getScore(ChatColor.GOLD + local.keyWordGeneralMaximum + " = " + ChatColor.AQUA + String.valueOf(this.maxAmountPlayer)).setScore(3);
        this.users.clear();
        this.roundNo = 0;
        this.stallingAmount = 0;
        this.currentTile = 0;
        this.startTime = 0L;
        this.gameState = GameState.READY;
        DacSign.updateSigns(this);
    }
    
    public CharSequence getPlayerListToDisplay(final Language localInstance) {
        String playerListToDisplay = this.users.get(0).getPlayer().getDisplayName();
        for (int i = 1; i < this.users.size(); ++i) {
            if (i < this.users.size() - 1) {
                playerListToDisplay = String.valueOf(playerListToDisplay) + localInstance.keyWordGeneralComma + this.users.get(i).getPlayer().getDisplayName();
            }
            else {
                playerListToDisplay = String.valueOf(playerListToDisplay) + localInstance.keyWordGeneralAnd + this.users.get(i).getPlayer().getDisplayName();
            }
        }
        return playerListToDisplay;
    }
    
    private void setTotalTile() {
        this.totalTile = 0;
        for (int i = this.minPoint.getBlockX(); i <= this.maxPoint.getBlockX(); ++i) {
            for (int k = this.minPoint.getBlockZ(); k <= this.maxPoint.getBlockZ(); ++k) {
                for (int j = this.maxPoint.getBlockY(); j >= this.minPoint.getBlockY(); --j) {
                    final Block block = new Location(this.world, (double)i, (double)j, (double)k).getBlock();
                    if (block.isLiquid()) {
                        ++this.totalTile;
                        break;
                    }
                    if (block.getType() != Material.AIR) {
                        break;
                    }
                }
            }
        }
    }

    public void fillWater() {
        for (int x = this.minPoint.getBlockX(); x <= this.maxPoint.getBlockX(); ++x) {
            for (int y = this.maxPoint.getBlockY(); y >= this.minPoint.getBlockY(); --y) {
                for (int z = this.minPoint.getBlockZ(); z <= this.maxPoint.getBlockZ(); ++z) {
                    final Location location = new Location(this.world, (double)x, (double)y, (double)z);
                    final Block block = location.getBlock();
                    if (Tag.WOOL.isTagged(block.getType()) || block.getType().toString().contains(Material.TERRACOTTA.toString()) || block.getType().toString().contains("STAINED_GLASS")) {
                        for (final ItemStackManager item : this.colorManager.getOnlyChoosenBlocks()) {
                            if ((item.getMaterial() == block.getType() || block.getType().toString().contains("STAINED_GLASS")) && item.getItem().getDurability() == block.getData()) {
                                block.setType(Material.WATER);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void resetArena(final ItemStack item) {
        for (int x = this.minPoint.getBlockX(); x <= this.maxPoint.getBlockX(); ++x) {
            for (int y = this.maxPoint.getBlockY(); y >= this.minPoint.getBlockY(); --y) {
                for (int z = this.minPoint.getBlockZ(); z <= this.maxPoint.getBlockZ(); ++z) {
                    final Location location = new Location(this.world, (double)x, (double)y, (double)z);
                    final Block block = location.getBlock();
                    if (Tag.WOOL.isTagged(block.getType()) || block.getType().toString().contains(Material.TERRACOTTA.toString()) || block.getType().toString().contains("STAINED_GLASS")) {
                        if ((item.getType() == block.getType() || block.getType().toString().contains("STAINED_GLASS")) && ColorManager.getData(item.getType()) == ColorManager.getData(block.getType())) {
                            block.setType(Material.WATER);
                        }
                    }
                }
            }
        }
    }
    
    public User getUser(final Player p) {
        for (final User user : this.users) {
            if (user.getPlayer() == p) {
                return user;
            }
        }
        return null;
    }
    
    private User lastPlayer() {
        User user = new User(0);
        for (final User u : this.getNonEliminated()) {
            if (u.getPlace() > user.getPlace()) {
                user = u;
            }
        }
        return user;
    }
    
    public boolean isLastPlayer(final User user) {
        return this.lastPlayer().getPlace() <= user.getPlace();
    }
    
    private User firstPlayer() {
        User user = new User(this.maxAmountPlayer);
        for (final User u : this.getNonEliminated()) {
            if (u.getPlace() < user.getPlace()) {
                user = u;
            }
        }
        return user;
    }
    
    public User getNextPlayer() {
        if (this.isLastPlayer(this.activePlayer)) {
            return this.firstPlayer();
        }
        User nextPlayer = new User(this.maxAmountPlayer);
        for (final User user : this.getNonEliminated()) {
            if (user.getPlace() > this.activePlayer.getPlace() && user.getPlace() <= nextPlayer.getPlace()) {
                nextPlayer = user;
            }
        }
        return nextPlayer;
    }
    
    public boolean isOver() {
        return this.getNonEliminated().size() < 2;
    }
    
    public List<User> getUsers() {
        return this.users;
    }
    
    public void bumpCurrentTile() {
        ++this.currentTile;
    }
    
    public Location getMinPoolPoint() {
        return this.minPoint;
    }
    
    public Location getMaxPoolPoint() {
        return this.maxPoint;
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }
    
    public int getMinPlayer() {
        return this.minAmountPlayer;
    }
    
    public int getMaxPlayer() {
        return this.maxAmountPlayer;
    }
    
    public Location getLobby() {
        return this.lobby;
    }
    
    public Location getPlateform() {
        return this.plateform;
    }
    
    public boolean isDacFinished() {
        return this.currentTile == this.totalTile;
    }
    
    public void resetStallingAmount() {
        this.stallingAmount = 0;
    }
    
    public void bumpStallingAmount() {
        ++this.stallingAmount;
    }
    
    private void newRound() {
        final Language l = Arena.playerData.getLanguage(Arena.config.language);
        this.scoreboard.resetScores(ChatColor.GOLD + l.keyWordScoreboardRound + " = " + ChatColor.AQUA + String.valueOf(this.roundNo));
        ++this.roundNo;
        this.objective.getScore(ChatColor.GOLD + l.keyWordScoreboardRound + " = " + ChatColor.AQUA + String.valueOf(this.roundNo)).setScore(99);
        if (Arena.config.verbose) {
            for (final User p : this.users) {
                final Language local = Arena.playerData.getLanguageOfPlayer(p);
                local.sendMsg(p.getPlayer(), local.gameNewRound.replace("%round%", String.valueOf(this.roundNo)));
            }
        }
    }
    
    public boolean isSomeoneSurvived() {
        for (final User p : this.getNonEliminated()) {
            if (p.isRoundSuccess()) {
                return true;
            }
        }
        return false;
    }
    
    public void setGameState(final GameState gameState) {
        this.gameState = gameState;
    }
    
    public int getRoundNo() {
        return this.roundNo;
    }
    
    public boolean isSomeoneLostFinal() {
        return this.someoneLostFinal;
    }
    
    public void setSomeoneLostFinal(final boolean someoneLostFinal) {
        this.someoneLostFinal = someoneLostFinal;
    }
    
    public boolean isForceStart() {
        return this.forceStart;
    }
    
    public void countdown(final Arena arena, final int time) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)Arena.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (arena.gameState != GameState.STARTUP) {
                    for (final User user : arena.getUsers()) {
                        user.maxStats(false);
                        final Player player = user.getPlayer();
                        final Language local = Arena.playerData.getLanguageOfPlayer(player);
                        local.sendMsg(player, local.startStopped);
                    }
                    return;
                }
                final int level = (int)Math.floor(time / 20) + 1;
                for (final User user2 : arena.getUsers()) {
                    user2.getPlayer().setLevel(level);
                    user2.getPlayer().setExp((float)(time % 20 / 20.0));
                }
                switch (time / 20) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 10:
                    case 30: {
                        if (time % 20.0 == 0.0) {
                            for (final User user2 : arena.getUsers()) {
                                final Sound sound = DeACoudre.aboveOneNine ? Sound.valueOf("UI_BUTTON_CLICK") : Sound.valueOf("CLICK");
                                user2.getPlayer().playSound(user2.getPlayer().getLocation(), sound, 1.0f, 1.0f);
                                final Language local2 = Arena.playerData.getLanguageOfPlayer(user2);
                                Utils.sendTitle(user2.getPlayer(), JsonBuilder.getJson(new JsonBuilder.JsonElement(String.valueOf(time / 20), ChatColor.GOLD, true, false, false, false, false)), JsonBuilder.getJson(new JsonBuilder.JsonElement(local2.keyWordGeneralSeconds, ChatColor.DARK_GRAY, false, true, false, false, false)), 5, 10, 5);
                            }
                            break;
                        }
                        break;
                    }
                    case 0: {
                        if (time % 20.0 == 0.0) {
                            for (final User user2 : Arena.this.users) {
                                user2.maxStats(true);
                            }
                            arena.startGame(false);
                            return;
                        }
                        break;
                    }
                }
                arena.countdown(arena, time - 1);
            }
        }, 1L);
    }
    
    public void timeOut(final User user, final Arena arena, final int time, final int round) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)Arena.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                final Player player = user.getPlayer();
                if (user != arena.activePlayer || arena.gameState != GameState.ACTIVE || arena.getRoundNo() != round) {
                    return;
                }
                final Language local = Arena.playerData.getLanguageOfPlayer(user);
                if (time == 0) {
                    user.getPlayer().teleport(Arena.this.lobby);
                    user.maxStats(true);
                    if (!arena.forceStart) {
                        Arena.achievements.testAchievement(".challenges.longTime", user.getPlayer());
                    }
                    if (Arena.config.timeOutKick) {
                        local.sendMsg(player, local.gameTimeOutPlayer);
                        for (final User p : arena.users) {
                            if (user != p) {
                                final Language localInstance = Arena.playerData.getLanguageOfPlayer(p);
                                localInstance.sendMsg(p.getPlayer(), localInstance.gameTimeOutOthers.replace("%player%", user.getDisplayName()));
                            }
                        }
                        Arena.this.eliminateUser(user);
                        if (arena.isOver()) {
                            arena.getActivePlayer().getPlayer().teleport(arena.getLobby());
                            arena.finishGame(false);
                        }
                        else {
                            arena.nextPlayer();
                        }
                    }
                    else {
                        Arena.playerDamage.losingAlgorithm(player, arena, user);
                    }
                    return;
                }
                final int level = (int)Math.floor(time / 20) + 1;
                player.setLevel(level);
                player.setExp((float)(time % 20 / 20.0));
                switch (time / 20) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10: {
                        if (time % 20 == 0) {
                            final Sound sound = DeACoudre.aboveOneNine ? Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP") : Sound.valueOf("ORB_PICKUP");
                            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                            break;
                        }
                        break;
                    }
                }
                Arena.this.timeOut(user, arena, time - 1, round);
            }
        }, 1L);
    }
    
    public static List<Arena> getArenas() {
        return Arena.arenas;
    }
    
    public static Arena getArenaFromName(final String arenaName) {
        for (final Arena arena : Arena.arenas) {
            if (arena.getName().equalsIgnoreCase(arenaName)) {
                return arena;
            }
        }
        return null;
    }
    
    public static Arena getArenaFromPlayer(final Player player) {
        for (final Arena a : Arena.arenas) {
            for (final User p : a.users) {
                if (player == p.getPlayer()) {
                    return a;
                }
            }
        }
        return null;
    }
    
    public static List<Player> getAllPlayersInStartedGame() {
        final List<Player> inGame = new ArrayList<Player>();
        for (final Arena arena : Arena.arenas) {
            if (arena.gameState == GameState.ACTIVE) {
                for (final User player : arena.users) {
                    inGame.add(player.getPlayer());
                }
            }
        }
        return inGame;
    }
    
    public static List<Player> getAllOutsideGame(final Arena arena) {
        final List<Player> outsideGame = new ArrayList<Player>();
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            final Arena a = getArenaFromPlayer(player);
            if (a != null && getArenaFromPlayer(player) == arena) {
                continue;
            }
            outsideGame.add(player);
        }
        return outsideGame;
    }
    
    public GameState getGameState() {
        return this.gameState;
    }
    
    public ColorManager getColorManager() {
        return this.colorManager;
    }
    
    public List<User> getNonEliminated() {
        final List<User> nonEliminated = new ArrayList<User>();
        for (final User user : this.users) {
            if (!user.isEliminated()) {
                nonEliminated.add(user);
            }
        }
        return nonEliminated;
    }
    
    public Objective getObjective() {
        return this.objective;
    }
    
    public Team getSpectator() {
        return this.spectator;
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    static /* synthetic */ void access$1(final Arena arena, final User activePlayer) {
        arena.activePlayer = activePlayer;
    }
}
