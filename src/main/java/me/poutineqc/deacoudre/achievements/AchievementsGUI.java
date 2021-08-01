// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.achievements;

import java.util.Iterator;
import java.sql.ResultSet;
import java.util.ArrayList;
import me.poutineqc.deacoudre.tools.ItemStackManager;
import java.sql.SQLException;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import me.poutineqc.deacoudre.Language;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.poutineqc.deacoudre.DeACoudre;
import me.poutineqc.deacoudre.MySQL;
import me.poutineqc.deacoudre.PlayerData;
import me.poutineqc.deacoudre.Configuration;
import org.bukkit.event.Listener;

public class AchievementsGUI implements Listener
{
    private Configuration config;
    private PlayerData playerData;
    private Achievement achievement;
    private MySQL mysql;
    
    public AchievementsGUI(final DeACoudre plugin) {
        this.config = plugin.getConfiguration();
        this.mysql = plugin.getMySQL();
        this.playerData = plugin.getPlayerData();
        this.achievement = plugin.getAchievement();
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Inventory inv = event.getInventory();
        final InventoryView invView = event.getView();
        final Player player = (Player)event.getWhoClicked();
        final Language local = this.playerData.getLanguageOfPlayer(player);
        if (ChatColor.stripColor(invView.getTitle()).equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStats))) || ChatColor.stripColor(invView.getTitle()).equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordChallenges)))) {
            if (event.getAction().equals((Object)InventoryAction.NOTHING) || event.getAction().equals((Object)InventoryAction.UNKNOWN)) {
                return;
            }
            event.setCancelled(true);
            final ItemStack item = event.getCurrentItem();
            if (item.getType() != Material.ARROW) {
                return;
            }
            final String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            if (ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordChallenges)))) {
                this.openChallenges(player);
                return;
            }
            if (ChatColor.stripColor(itemName).equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStats)))) {
                this.openStats(player);
            }
        }
    }
    
    public void openStats(final Player player) {
        final Language local = this.playerData.getLanguageOfPlayer(player);
        final String UUID = player.getUniqueId().toString();
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', local.keyWordStats));
        int gamesPlayed = 0;
        int gamesWon = 0;
        int gamesLost = 0;
        int DaCdone = 0;
        int timePlayed = 0;
        double moneyGains = 0.0;
        if (this.mysql.hasConnection()) {
            final ResultSet query = this.mysql.query("SELECT gamesPlayed, gamesWon, gamesLost, DaCdone, timePlayed, money FROM " + this.config.tablePrefix + "PLAYERS WHERE UUID='" + UUID + "';");
            try {
                if (query.next()) {
                    gamesPlayed = query.getInt("gamesPlayed");
                    gamesWon = query.getInt("gamesWon");
                    gamesLost = query.getInt("gamesLost");
                    DaCdone = query.getInt("DaCdone");
                    timePlayed = query.getInt("timePlayed");
                    moneyGains = query.getDouble("money");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            gamesPlayed = this.playerData.getData().getInt("players." + UUID + ".gamesPlayed", 0);
            gamesWon = this.playerData.getData().getInt("players." + UUID + ".gamesWon", 0);
            gamesLost = this.playerData.getData().getInt("players." + UUID + ".gamesPlayed", 0) - this.playerData.getData().getInt("players." + UUID + ".gamesWon", 0);
            DaCdone = this.playerData.getData().getInt("players." + UUID + ".DaCdone", 0);
            timePlayed = this.playerData.getData().getInt("players." + UUID + ".stats.timePlayed", 0);
            moneyGains = this.playerData.getData().getInt("players." + UUID + ".stats.moneyGains", 0);
        }
        moneyGains = Math.floor(moneyGains * 100.0) / 100.0;
        ItemStackManager icon = new ItemStackManager(Material.PAPER, 4);
        icon.setTitle(new StringBuilder().append(ChatColor.UNDERLINE).append(ChatColor.GOLD).append(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', String.valueOf(local.keyWordStats) + " : DaC"))).toString());
        icon.addToLore("&e---------------------------");
        icon.addToLore(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsGamesPlayed)) + ": " + ChatColor.YELLOW + gamesPlayed);
        icon.addToLore(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsGamesWon)) + ": " + ChatColor.YELLOW + gamesWon);
        icon.addToLore(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsGamesLost)) + ": " + ChatColor.YELLOW + gamesLost);
        icon.addToLore(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsDacsDone)) + ": " + ChatColor.YELLOW + DaCdone);
        icon.addToLore(ChatColor.YELLOW + "---------------------------");
        icon.addToLore(ChatColor.LIGHT_PURPLE + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsTimePlayed)) + ": " + this.getTimePLayed(local, timePlayed));
        if (this.config.economyReward) {
            icon.addToLore(ChatColor.LIGHT_PURPLE + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsMoneyGot)) + ": " + ChatColor.YELLOW + DeACoudre.getEconomy().currencyNamePlural() + moneyGains);
        }
        inv = icon.addToInventory(inv);
        icon = new ItemStackManager(Material.ARROW, 8);
        icon.setTitle(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordChallenges)));
        inv = icon.addToInventory(inv);
        icon = new ItemStackManager(Material.MAP, 18);
        icon.setTitle(ChatColor.GOLD + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsTop10)) + " : " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsGamesPlayed)));
        icon.addToLore(ChatColor.YELLOW + "---------------------------");
        if (this.mysql.hasConnection()) {
            final ResultSet query = this.mysql.query("SELECT * FROM " + this.config.tablePrefix + "GAMESPLAYED;");
            try {
                while (query.next()) {
                    icon.addToLore(ChatColor.LIGHT_PURPLE + query.getString("name") + " : " + ChatColor.YELLOW + query.getInt("gamesPlayed"));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            for (int i = 0; i < 10 && i < TopManager.getGames().size(); ++i) {
                icon.addToLore(ChatColor.LIGHT_PURPLE + TopManager.getGames().get(i).getPlayer() + " : " + ChatColor.YELLOW + TopManager.getGames().get(i).getScore());
            }
        }
        inv = icon.addToInventory(inv);
        icon = new ItemStackManager(Material.MAP, 27);
        icon.setTitle(ChatColor.GOLD + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsTop10)) + " : " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsGamesWon)));
        icon.addToLore(ChatColor.YELLOW + "---------------------------");
        if (this.mysql.hasConnection()) {
            final ResultSet query = this.mysql.query("SELECT * FROM " + this.config.tablePrefix + "GAMESWON;");
            try {
                while (query.next()) {
                    icon.addToLore(ChatColor.LIGHT_PURPLE + query.getString("name") + " : " + ChatColor.YELLOW + query.getInt("gamesWon"));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            for (int i = 0; i < 10 && i < TopManager.getWon().size(); ++i) {
                icon.addToLore(ChatColor.LIGHT_PURPLE + TopManager.getWon().get(i).getPlayer() + " : " + ChatColor.YELLOW + TopManager.getWon().get(i).getScore());
            }
        }
        inv = icon.addToInventory(inv);
        icon = new ItemStackManager(Material.MAP, 36);
        icon.setTitle(ChatColor.GOLD + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsTop10)) + " : " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsGamesLost)));
        icon.addToLore(ChatColor.YELLOW + "---------------------------");
        if (this.mysql.hasConnection()) {
            final ResultSet query = this.mysql.query("SELECT * FROM " + this.config.tablePrefix + "GAMESLOST;");
            try {
                while (query.next()) {
                    icon.addToLore(ChatColor.LIGHT_PURPLE + query.getString("name") + " : " + ChatColor.YELLOW + query.getInt("gamesLost"));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            for (int i = 0; i < 10 && i < TopManager.getLost().size(); ++i) {
                icon.addToLore(ChatColor.LIGHT_PURPLE + TopManager.getLost().get(i).getPlayer() + " : " + ChatColor.YELLOW + TopManager.getLost().get(i).getScore());
            }
        }
        inv = icon.addToInventory(inv);
        icon = new ItemStackManager(Material.MAP, 45);
        icon.setTitle(ChatColor.GOLD + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsTop10)) + " : " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsDacsDone)));
        icon.addToLore(ChatColor.YELLOW + "---------------------------");
        if (this.mysql.hasConnection()) {
            final ResultSet query = this.mysql.query("SELECT * FROM " + this.config.tablePrefix + "DACDONE;");
            try {
                while (query.next()) {
                    icon.addToLore(ChatColor.LIGHT_PURPLE + query.getString("name") + " : " + ChatColor.YELLOW + query.getInt("DaCdone"));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            for (int i = 0; i < 10 && i < TopManager.getDaCdone().size(); ++i) {
                icon.addToLore(ChatColor.LIGHT_PURPLE + TopManager.getDaCdone().get(i).getPlayer() + " : " + ChatColor.YELLOW + TopManager.getDaCdone().get(i).getScore());
            }
        }
        icon.addToInventory(inv);
        icon = new ItemStackManager(Material.ORANGE_STAINED_GLASS_PANE);
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
                case 19:
                case 28:
                case 37:
                case 46: {
                    icon.setPosition(i);
                    inv = icon.addToInventory(inv);
                    break;
                }
            }
        }
        final String[] challengeNames = { local.challengeDisplayPlayed, local.challengeDisplayWin, local.challengeDisplayLost, local.challengeDisplayDaC };
        final String[] challengePath = { ".gamesPlayed", ".gamesWon", ".gamesLost", ".DaCdone" };
        final ArrayList<ArrayList<AchievementsObject>> achievements = this.achievement.get_achievements();
        for (int j = 0; j < 4; ++j) {
            int position = j * 9 + 20;
            for (final AchievementsObject ao : achievements.get(j)) {
                icon = new ItemStackManager(Material.GRAY_WOOL, position++);
                int amount = 0;
                if (this.mysql.hasConnection()) {
                    final ResultSet query2 = this.mysql.query("SELECT " + challengePath[j].substring(1) + " FROM " + this.config.tablePrefix + "PLAYERS WHERE UUID='" + UUID + "';");
                    try {
                        if (query2.next()) {
                            amount = query2.getInt(challengePath[j].substring(1));
                        }
                    }
                    catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                }
                else {
                    amount = this.playerData.getData().getInt("players." + UUID + challengePath[j], 0);
                }
                if (amount >= ao.get_level()) {
                    icon = new ItemStackManager(Material.GREEN_WOOL, position++);
                    icon.setTitle(ChatColor.GREEN + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', challengeNames[j].replace("%amount%", String.valueOf(ao.get_level())))));
                    icon.addToLore(ChatColor.YELLOW + "---------------------------");
                    icon.addToLore(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', local.keyWordStatsProgression) + ": " + ChatColor.GREEN + local.keyWordStatsCompleted);
                }
                else {
                    icon = new ItemStackManager(Material.LIGHT_GRAY_WOOL, position++);
                    icon.setTitle(ChatColor.RED + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', challengeNames[j].replace("%amount%", String.valueOf(ao.get_level())))));
                    icon.addToLore(ChatColor.YELLOW + "---------------------------");
                    icon.addToLore(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', local.keyWordStatsProgression) + ": " + ChatColor.YELLOW + String.valueOf(amount) + "/" + ao.get_level());
                }
                if (this.config.challengeReward && this.config.economyReward) {
                    icon.addToLore(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', local.keyWordStatsReward) + ": " + ChatColor.YELLOW + DeACoudre.getEconomy().currencyNamePlural() + ao.get_reward());
                }
                icon.addToInventory(inv);
            }
        }
        player.openInventory(inv);
    }
    
    public void openChallenges(final Player player) {
        final Language local = this.playerData.getLanguageOfPlayer(player);
        final String UUID = player.getUniqueId().toString();
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordChallenges)));
        int gamesPlayed = 0;
        int gamesWon = 0;
        int gamesLost = 0;
        int DaCdone = 0;
        int timePlayed = 0;
        double moneyGains = 0.0;
        if (this.mysql.hasConnection()) {
            final ResultSet query = this.mysql.query("SELECT gamesPlayed, gamesWon, gamesLost, DaCdone, timePlayed, money FROM " + this.config.tablePrefix + "PLAYERS WHERE UUID='" + UUID + "';");
            try {
                if (query.next()) {
                    gamesPlayed = query.getInt("gamesPlayed");
                    gamesWon = query.getInt("gamesWon");
                    gamesLost = query.getInt("gamesLost");
                    DaCdone = query.getInt("DaCdone");
                    timePlayed = query.getInt("timePlayed");
                    moneyGains = query.getDouble("money");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            gamesPlayed = this.playerData.getData().getInt("players." + UUID + ".gamesPlayed", 0);
            gamesWon = this.playerData.getData().getInt("players." + UUID + ".gamesWon", 0);
            gamesLost = this.playerData.getData().getInt("players." + UUID + ".gamesPlayed", 0) - this.playerData.getData().getInt("players." + UUID + ".gamesWon", 0);
            DaCdone = this.playerData.getData().getInt("players." + UUID + ".DaCdone", 0);
            timePlayed = this.playerData.getData().getInt("players." + UUID + ".stats.timePlayed", 0);
            moneyGains = this.playerData.getData().getInt("players." + UUID + ".stats.moneyGains", 0);
        }
        moneyGains = Math.floor(moneyGains * 100.0) / 100.0;
        ItemStackManager icon = new ItemStackManager(Material.PAPER, 4);
        icon.setTitle(new StringBuilder().append(ChatColor.UNDERLINE).append(ChatColor.GOLD).append(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', String.valueOf(local.keyWordStats) + " : DaC"))).toString());
        icon.addToLore("&e---------------------------");
        icon.addToLore(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsGamesPlayed)) + ": " + ChatColor.YELLOW + gamesPlayed);
        icon.addToLore(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsGamesWon)) + ": " + ChatColor.YELLOW + gamesWon);
        icon.addToLore(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsGamesLost)) + ": " + ChatColor.YELLOW + gamesLost);
        icon.addToLore(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsDacsDone)) + ": " + ChatColor.YELLOW + DaCdone);
        icon.addToLore(ChatColor.YELLOW + "---------------------------");
        icon.addToLore(ChatColor.LIGHT_PURPLE + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsTimePlayed)) + ": " + this.getTimePLayed(local, timePlayed));
        if (this.config.economyReward) {
            icon.addToLore(ChatColor.LIGHT_PURPLE + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsMoneyGot)) + ": " + ChatColor.YELLOW + DeACoudre.getEconomy().currencyNamePlural() + moneyGains);
        }
        inv = icon.addToInventory(inv);
        icon = new ItemStackManager(Material.ORANGE_STAINED_GLASS_PANE);
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
                case 17: {
                    icon.setPosition(i);
                    inv = icon.addToInventory(inv);
                    break;
                }
            }
        }
        final String[] challengeNames = { local.challengeDisplayCompleteArena, local.challengeDisplay8PlayersGame, local.challengeDisplayReachRound100, local.challengeDisplayFight, local.challengeDisplayAnswerToLife, local.challengeDisplayMinecraftSnail };
        final String[] challengePath = { ".challenges.completedArena", ".challenges.8playersGame", ".challenges.reachRound100", ".challenges.colorRivalery", ".challenges.DaCon42", ".challenges.longTime" };
        final double[] challengeReward = { this.config.challengeRewardFinishArenaFirstTime, this.config.challengeReward8PlayersGame, this.config.challengeRewardReachRound100, this.config.hiddenChallengeReward, this.config.hiddenChallengeReward, this.config.hiddenChallengeReward };
        int position = 19;
        for (int j = 0; j < 6; ++j) {
            icon = new ItemStackManager(Material.INK_SAC, position++);
            boolean completed = false;
            if (this.mysql.hasConnection()) {
                final ResultSet query2 = this.mysql.query("SELECT " + challengePath[j].substring(12) + " FROM " + this.config.tablePrefix + "PLAYERS WHERE UUID='" + UUID + "';");
                try {
                    if (query2.next()) {
                        completed = query2.getBoolean(challengePath[j].substring(12));
                    }
                }
                catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
            else {
                completed = this.playerData.getData().getBoolean("players." + UUID + challengePath[j]);
            }
            if (completed) {
                icon.setData((short)10);
                icon.setTitle(ChatColor.GREEN + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', challengeNames[j])));
                icon.addToLore(ChatColor.YELLOW + "---------------------------");
                icon.addToLore(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', local.keyWordStatsProgression) + ": " + ChatColor.GREEN + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsCompleted)));
            }
            else {
                icon.setData((short)8);
                icon.setTitle(ChatColor.RED + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', challengeNames[j])));
                icon.addToLore(ChatColor.YELLOW + "---------------------------");
                icon.addToLore(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', local.keyWordStatsProgression) + ": " + ChatColor.RED + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStatsNotCompleted)));
            }
            if (this.config.challengeReward && this.config.economyReward) {
                icon.addToLore(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', local.keyWordStatsReward) + ": " + ChatColor.YELLOW + DeACoudre.getEconomy().currencyNamePlural() + challengeReward[j]);
            }
            inv = icon.addToInventory(inv);
            if (position == 22) {
                ++position;
            }
        }
        icon = new ItemStackManager(Material.ARROW, 8);
        icon.setTitle(ChatColor.AQUA + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordStats)));
        inv = icon.addToInventory(inv);
        player.openInventory(inv);
    }
    
    private String getTimePLayed(final Language local, int timePlayed) {
        timePlayed = (int)Math.floor(timePlayed / 60000.0);
        final int minutes = timePlayed % 60;
        timePlayed = (int)Math.floor(timePlayed / 60.0);
        return ChatColor.YELLOW + String.valueOf(timePlayed) + ChatColor.GREEN + " " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordGeneralHours)) + ChatColor.YELLOW + " " + String.valueOf(minutes) + ChatColor.GREEN + " " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordGeneralMinutes));
    }
}
