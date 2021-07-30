// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.achievements;

import java.util.Collection;
import org.bukkit.Bukkit;
import me.poutineqc.deacoudre.Language;
import java.sql.ResultSet;
import java.util.Iterator;
import org.bukkit.OfflinePlayer;
import java.sql.SQLException;
import org.bukkit.entity.Player;
import me.poutineqc.deacoudre.instances.User;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.InputStream;
import me.poutineqc.deacoudre.MySQL;
import me.poutineqc.deacoudre.PlayerData;
import me.poutineqc.deacoudre.Configuration;
import java.util.ArrayList;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import me.poutineqc.deacoudre.DeACoudre;

public class Achievement
{
    public static final String gamesPlayed = ".gamesPlayed";
    public static final String gamesWon = ".gamesWon";
    public static final String gamesLost = ".gamesLost";
    public static final String dacDone = ".DaCdone";
    public static final String completedArena = ".challenges.completedArena";
    public static final String eightPlayersGame = ".challenges.8playersGame";
    public static final String reachRoundHundred = ".challenges.reachRound100";
    public static final String dacOnFortyTwo = ".challenges.DaCon42";
    public static final String colorRivalery = ".challenges.colorRivalery";
    public static final String longTime = ".challenges.longTime";
    private DeACoudre plugin;
    private FileConfiguration achievementData;
    private File achievementDataFile;
    private ArrayList<ArrayList<AchievementsObject>> achievements;
    private Configuration config;
    private PlayerData playerData;
    private MySQL mysql;
    
    public Achievement(final DeACoudre plugin) {
        this.achievements = new ArrayList<ArrayList<AchievementsObject>>();
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.mysql = plugin.getMySQL();
        this.playerData = plugin.getPlayerData();
        this.achievementDataFile = new File(plugin.getDataFolder(), "achievements.yml");
        if (!this.achievementDataFile.exists()) {
            final InputStream local = plugin.getResource("achievements.yml");
            if (local != null) {
                plugin.saveResource("achievements.yml", false);
            }
            else {
                plugin.getLogger().info("Could not find achievements.yml - Using default (en-US)");
            }
        }
        this.load_achievements();
    }
    
    public void load_achievements() {
        this.achievementData = (FileConfiguration)YamlConfiguration.loadConfiguration(this.achievementDataFile);
        this.achievements.clear();
        final String[] configNames = { "amountOfGamesPlayed", "amountOfGamesWon", "amountOfGamesLost", "amountOfDaCsDone" };
        for (int i = 0; i < 4; ++i) {
            this.achievements.add(new ArrayList<AchievementsObject>());
            final List<String> readData = (List<String>)this.achievementData.getStringList(configNames[i]);
            for (int j = 0; j < readData.size(); ++j) {
                final String[] individualData = readData.get(j).split(";");
                if (individualData.length < 2) {
                    this.plugin.getLogger().info("Could not load the " + j + "'th data from the " + i + "'th achievement type.1");
                }
                else {
                    try {
                        final int level = Integer.parseInt(individualData[0]);
                        final double reward = Double.parseDouble(individualData[1]);
                        this.achievements.get(i).add(new AchievementsObject(level, reward));
                    }
                    catch (NumberFormatException e) {
                        this.plugin.getLogger().info("Could not load the " + j + "'th data from the " + i + "'th achievement type.2");
                    }
                }
            }
        }
    }
    
    public ArrayList<ArrayList<AchievementsObject>> get_achievements() {
        return this.achievements;
    }
    
    public void testAchievement(final String toCheck, final User user) {
        this.testAchievement(toCheck, user.getPlayer());
    }
    
    public void testAchievement(final String toCheck, final Player player) {
        final Achievement achievement = this.plugin.getAchievement();
        double rewardAmount = 0.0;
        Label_0993: {
            Label_0483: {
                switch (toCheck) {
                    case ".gamesLost": {
                        break;
                    }
                    case ".gamesWon": {
                        break;
                    }
                    case ".gamesPlayed": {
                        break;
                    }
                    case ".DaCdone": {
                        break;
                    }
                    default:
                        break Label_0483;
                }
                int listNo = 0;
                switch (toCheck) {
                    case ".gamesLost": {
                        listNo = 2;
                        break;
                    }
                    case ".gamesWon": {
                        listNo = 1;
                        break;
                    }
                    case ".gamesPlayed": {
                        listNo = 0;
                        break;
                    }
                    case ".DaCdone": {
                        listNo = 3;
                        break;
                    }
                    default:
                        break;
                }
                for (final AchievementsObject ao : achievement.get_achievements().get(listNo)) {
                    int level = 0;
                    if (this.mysql.hasConnection()) {
                        final ResultSet query = this.mysql.query("SELECT " + toCheck.substring(1) + " FROM " + this.config.tablePrefix + "PLAYERS WHERE UUID='" + player.getUniqueId().toString() + "';");
                        try {
                            if (query.next()) {
                                level = query.getInt(toCheck.substring(1));
                            }
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        level = this.playerData.getData().getInt("players." + player.getUniqueId().toString() + toCheck, 0);
                    }
                    if (level != ao.get_level()) {
                        continue;
                    }
                    this.sendCongratulationMessage(player, toCheck, listNo, ao);
                    if (DeACoudre.isEconomyEnabled()) {
                        rewardAmount = ao.get_reward();
                        break;
                    }
                    break;
                }
                break Label_0993;
            }
            boolean completed = true;
            if (this.mysql.hasConnection()) {
                final ResultSet query2 = this.mysql.query("SELECT " + toCheck.substring(12) + " FROM " + this.config.tablePrefix + "PLAYERS WHERE UUID='" + player.getUniqueId().toString() + "';");
                try {
                    if (query2.next()) {
                        completed = query2.getBoolean(toCheck.substring(12));
                    }
                }
                catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
            else {
                completed = this.playerData.getData().getBoolean("players." + player.getUniqueId().toString() + toCheck, false);
            }
            if (!completed) {
                if (this.mysql.hasConnection()) {
                    this.mysql.update("UPDATE " + this.config.tablePrefix + "PLAYERS SET " + toCheck.substring(12) + "='1' WHERE UUID='" + player.getUniqueId().toString() + "';");
                }
                else {
                    this.playerData.getData().set("players." + player.getUniqueId().toString() + toCheck, (Object)true);
                    this.playerData.savePlayerData();
                }
                this.sendCongratulationMessage(this.plugin, player, toCheck);
                if (DeACoudre.isEconomyEnabled()) {
                    Label_0990: {
                        switch (toCheck) {
                            case ".challenges.completedArena": {
                                rewardAmount = this.config.challengeRewardFinishArenaFirstTime;
                                break Label_0993;
                            }
                            case ".challenges.reachRound100": {
                                rewardAmount = this.config.challengeRewardReachRound100;
                                break Label_0993;
                            }
                            case ".challenges.longTime": {
                                break;
                            }
                            case ".challenges.DaCon42": {
                                break;
                            }
                            case ".challenges.8playersGame": {
                                rewardAmount = this.config.challengeReward8PlayersGame;
                                break Label_0993;
                            }
                            case ".challenges.colorRivalery": {
                                break;
                            }
                            default:
                                break Label_0990;
                        }
                        rewardAmount = this.config.hiddenChallengeReward;
                        break Label_0993;
                    }
                    rewardAmount = 0.0;
                }
            }
        }
        if (rewardAmount != 0.0 && this.config.challengeReward && DeACoudre.isEconomyEnabled()) {
            DeACoudre.getEconomy().depositPlayer((OfflinePlayer)player, rewardAmount);
            final Language local = this.playerData.getLanguageOfPlayer(player);
            local.sendMsg(player, local.challengeRewardMoney.replace("%currency%", DeACoudre.getEconomy().currencyNamePlural()).replace("%amount%", String.valueOf(rewardAmount)));
            double previousAmount = 0.0;
            if (this.mysql.hasConnection()) {
                final ResultSet query2 = this.mysql.query("SELECT money FROM " + this.config.tablePrefix + "PLAYERS WHERE UUID='" + player.getUniqueId().toString() + "';");
                try {
                    if (query2.next()) {
                        previousAmount = query2.getDouble("money");
                    }
                }
                catch (SQLException e2) {
                    e2.printStackTrace();
                }
                this.mysql.update("UPDATE " + this.config.tablePrefix + "PLAYERS SET money='" + (previousAmount + rewardAmount) + "' WHERE UUID='" + player.getUniqueId().toString() + "';");
            }
            else {
                previousAmount = this.playerData.getData().getDouble("players." + player.getUniqueId().toString() + ".stats.moneyGains", 0.0);
                this.playerData.getData().set("players." + player.getUniqueId().toString() + ".stats.moneyGains", (Object)(previousAmount + rewardAmount));
                this.playerData.savePlayerData();
            }
        }
    }
    
    private void sendCongratulationMessage(final Player player, final String toCheck, final int listNo, final AchievementsObject ao) {
        if (this.plugin.getConfiguration().broadcastAchievements) {
            for (final Player p : this.getBroadcastList(player)) {
                final Language local = this.playerData.getLanguageOfPlayer(p);
                switch (toCheck.hashCode()) {
                    case 568752407: {
                        if (!toCheck.equals(".gamesLost")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplayLost.replace("%amount%", String.valueOf(ao.get_level()))));
                        continue;
                    }
                    case 988188739: {
                        if (!toCheck.equals(".gamesWon")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplayWin.replace("%amount%", String.valueOf(ao.get_level()))));
                        continue;
                    }
                    case 1221434374: {
                        if (!toCheck.equals(".gamesPlayed")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplayPlayed.replace("%amount%", String.valueOf(ao.get_level()))));
                        continue;
                    }
                    case 1627597786: {
                        if (!toCheck.equals(".DaCdone")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplayDaC.replace("%amount%", String.valueOf(ao.get_level()))));
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
    }
    
    private List<Player> getBroadcastList(final Player player) {
        List<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        if (!this.config.broadcastCongradulations) {
            players = new ArrayList<Player>();
            players.add(player);
        }
        return players;
    }
    
    private void sendCongratulationMessage(final DeACoudre plugin, final Player player, final String toCheck) {
        if (plugin.getConfiguration().broadcastAchievements) {
            for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                final Language local = this.playerData.getLanguageOfPlayer(p);
                switch (toCheck.hashCode()) {
                    case -2090754996: {
                        if (!toCheck.equals(".challenges.completedArena")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplayCompleteArena).toString());
                        continue;
                    }
                    case -1102991002: {
                        if (!toCheck.equals(".challenges.reachRound100")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplayReachRound100).toString());
                        continue;
                    }
                    case -86387655: {
                        if (!toCheck.equals(".challenges.longTime")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplayMinecraftSnail).toString());
                        continue;
                    }
                    case 356209171: {
                        if (!toCheck.equals(".challenges.DaCon42")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplayAnswerToLife).toString());
                        continue;
                    }
                    case 1895203420: {
                        if (!toCheck.equals(".challenges.8playersGame")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplay8PlayersGame).toString());
                        continue;
                    }
                    case 2031274325: {
                        if (!toCheck.equals(".challenges.colorRivalery")) {
                            continue;
                        }
                        local.sendMsg(p, local.challengeBroadcast.replace("%player%", player.getDisplayName()).replace("%challenge%", local.challengeDisplayFight).toString());
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
    }
}
