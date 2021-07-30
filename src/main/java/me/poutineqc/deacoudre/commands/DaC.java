// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.commands;

import java.util.List;
import me.poutineqc.deacoudre.guis.ColorsGUI;
import java.util.Map;
import me.poutineqc.deacoudre.Language;
import java.sql.ResultSet;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Iterator;
import org.bukkit.Bukkit;
import java.sql.SQLException;
import org.bukkit.plugin.Plugin;
import me.poutineqc.deacoudre.instances.GameState;
import me.poutineqc.deacoudre.instances.Arena;
import me.poutineqc.deacoudre.Permissions;
import org.bukkit.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.poutineqc.deacoudre.MySQL;
import me.poutineqc.deacoudre.achievements.AchievementsGUI;
import me.poutineqc.deacoudre.guis.JoinGUI;
import me.poutineqc.deacoudre.guis.ChooseColorGUI;
import me.poutineqc.deacoudre.PlayerData;
import me.poutineqc.deacoudre.ArenaData;
import me.poutineqc.deacoudre.Configuration;
import me.poutineqc.deacoudre.DeACoudre;
import org.bukkit.command.CommandExecutor;

public class DaC implements CommandExecutor
{
    private static DeACoudre plugin;
    private static Configuration config;
    private ArenaData arenaData;
    private static PlayerData playerData;
    private ChooseColorGUI chooseColorGUI;
    private JoinGUI joinGUI;
    private AchievementsGUI achievementsGUI;
    private MySQL mysql;
    private DacSign signData;
    
    public DaC(final DeACoudre plugin) {
        DaC.plugin = plugin;
        DaC.config = plugin.getConfiguration();
        this.mysql = plugin.getMySQL();
        this.arenaData = plugin.getArenaData();
        DaC.playerData = plugin.getPlayerData();
        this.signData = plugin.getSignData();
        this.chooseColorGUI = plugin.getChooseColorGUI();
        this.joinGUI = plugin.getJoinGUI();
        this.achievementsGUI = plugin.getAchievementsGUI();
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String cmdValue, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use DaC's commands.");
            return true;
        }
        final Player player = (Player)sender;
        Language local = DaC.playerData.getLanguageOfPlayer(player);
        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m" + StringUtils.repeat(" ", 90)));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', local.pluginDevelopper.replace("%developper%", DaC.plugin.getDescription().getAuthors().toString())));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', local.pluginVersion.replace("%version%", DaC.plugin.getDescription().getVersion())));
            local.sendMsg(player, local.pluginHelp.replace("%command%", cmdValue));
            player.sendMessage("\n");
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            this.sendHelp(player, cmdValue, args);
            return true;
        }
        DacCommand command = DacCommand.getCommand(args[0]);
        if (command != null) {
            final String cmdName = command.getCommandName();
            if (!Permissions.hasPermission(player, command, true)) {
                return true;
            }
            if (cmdName.equalsIgnoreCase("language")) {
                this.changeLanguage(player, args, cmdValue);
                return true;
            }
            if (cmdName.equalsIgnoreCase("info")) {
                this.displayInfo(player, args, cmdValue);
                return true;
            }
            if (cmdName.equalsIgnoreCase("list") || cmdName.equalsIgnoreCase("join") || cmdName.equalsIgnoreCase("play")) {
                if (args.length > 1) {
                    this.commandJoin(command, player, args.length, args[1], true);
                }
                else {
                    this.commandJoin(command, player, args.length, "", true);
                }
                return true;
            }
            if (cmdName.equalsIgnoreCase("color")) {
                this.openColorGUI(command, player);
                return true;
            }
            if (cmdName.equalsIgnoreCase("quit")) {
                this.quitGame(command, player);
                return true;
            }
            if (cmdName.equalsIgnoreCase("start")) {
                this.startGame(command, player);
                return true;
            }
            if (cmdName.equalsIgnoreCase("forcestart")) {
                final Arena arena = Arena.getArenaFromPlayer(player);
                if (arena == null) {
                    local.sendMsg(player, local.errorNotInGame);
                    return true;
                }
                if (arena.getGameState() != GameState.READY) {
                    local.sendMsg(player, local.errorGameStarted);
                    return true;
                }
                if (arena.getUsers().size() == 1) {
                    arena.startGame(true);
                }
                else {
                    local.sendMsg(player, local.forcestartError);
                }
                return true;
            }
            else {
                if (cmdName.equalsIgnoreCase("stats")) {
                    this.achievementsGUI.openStats(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("new")) {
                    switch (args.length) {
                        case 1: {
                            local.sendMsg(player, local.editNewNoName);
                            break;
                        }
                        case 2: {
                            final Arena arena = Arena.getArenaFromName(args[1]);
                            if (arena != null) {
                                local.sendMsg(player, local.editNewExists.replace("%arenaName%", args[1]));
                                break;
                            }
                            if (args[1].contains(".")) {
                                local.sendMsg(player, local.editNewLong);
                                break;
                            }
                            new Arena(args[1], player);
                            local.sendMsg(player, local.editNewSuccess.replace("%arenaName%", args[1]));
                            break;
                        }
                        default: {
                            local.sendMsg(player, local.editNewLong);
                            break;
                        }
                    }
                    return true;
                }
                if (cmdName.equalsIgnoreCase("reload")) {
                    if (this.mysql.hasConnection()) {
                        this.mysql.close();
                    }
                    DaC.config.loadConfig((Plugin)DaC.plugin);
                    if (DaC.config.mysql) {
                        this.mysql.updateInfo(DaC.plugin);
                    }
                    DaC.plugin.initialiseEconomy();
                    DaC.plugin.loadLanguages();
                    if (!this.mysql.hasConnection()) {
                        DaC.playerData.loadPlayerData();
                        this.arenaData.loadArenaData();
                    }
                    DaC.plugin.getAchievement().load_achievements();
                    Arena.loadArenas();
                    DacSign.loadAllSigns();
                    local = DaC.playerData.getLanguageOfPlayer(player);
                    local.sendMsg(player, local.reloadSucess);
                    return true;
                }
                if (!cmdName.equalsIgnoreCase("filetomysql")) {
                    local.sendMsg(player, local.editErrorNoArena);
                    return true;
                }
                if (!this.mysql.hasConnection()) {
                    local.sendMsg(player, local.convertNoMysql);
                    return true;
                }
                if (DaC.playerData.getData().getBoolean("converted", false)) {
                    local.sendMsg(player, local.convertAlreadyDone);
                    return true;
                }
                local.sendMsg(player, local.convertStart);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (DaC.playerData.getData().contains("players")) {
                            for (final String UUID : DaC.playerData.getData().getConfigurationSection("players").getKeys(false)) {
                                final ConfigurationSection sc = DaC.playerData.getData().getConfigurationSection("players." + UUID);
                                final String name = sc.getString("name");
                                final String language = sc.getString("language");
                                final int gamesPlayed = sc.getInt("gamesPlayed", 0);
                                final int gamesWon = sc.getInt("gamesWon", 0);
                                final int gamesLost = sc.getInt("gamesLost", 0);
                                final int DaCdone = sc.getInt("DaCdone", 0);
                                final Boolean completedArena = sc.getBoolean("challenges.completedArena", false);
                                final Boolean eightPlayersGame = sc.getBoolean("challenges.8playersGame", false);
                                final Boolean reachRound100 = sc.getBoolean("challenges.reachRound100", false);
                                final Boolean DaCon42 = sc.getBoolean("challenges.DaCon42", false);
                                final Boolean colorRivalery = sc.getBoolean("challenges.colorRivalery", false);
                                final Boolean longTime = sc.getBoolean("challenges.longTime", false);
                                final int timePlayed = sc.getInt("stats.timePlayed", 0);
                                final double moneyGains = sc.getDouble("stats.moneyGains", 0.0);
                                final ResultSet query = DaC.this.mysql.query("SELECT * FROM " + DaC.config.tablePrefix + "PLAYERS WHERE UUID='" + UUID + "';");
                                try {
                                    if (query.next()) {
                                        final int gamesPlayedNew = query.getInt("gamesPlayed");
                                        final int gamesWonNew = query.getInt("gamesWon");
                                        final int gamesLostNew = query.getInt("gamesLost");
                                        final int DaCdoneNew = query.getInt("DaCdone");
                                        final Boolean completedArenaNew = query.getBoolean("completedArena");
                                        final Boolean eightPlayersGameNew = query.getBoolean("8playersGame");
                                        final Boolean reachRound100New = query.getBoolean("reachRound100");
                                        final Boolean DaCon42New = query.getBoolean("DaCon42");
                                        final Boolean colorRivaleryNew = query.getBoolean("colorRivalery");
                                        final Boolean longTimeNew = query.getBoolean("longTime");
                                        final int timePlayedNew = query.getInt("timePlayed");
                                        final double moneyGainsNew = query.getDouble("money");
                                        DaC.this.mysql.update("UPDATE " + DaC.config.tablePrefix + "PLAYERS SET gamesPlayed='" + (gamesPlayed + gamesPlayedNew) + "', gamesWon='" + (gamesWon + gamesWonNew) + "', gamesLost='" + (gamesLost + gamesLostNew) + "', DaCdone='" + (DaCdone + DaCdoneNew) + "', completedArena='" + ((completedArena || completedArenaNew) ? "1" : " 0") + "', 8playersGame='" + ((eightPlayersGame || eightPlayersGameNew) ? "1" : " 0") + "', reachRound100='" + ((reachRound100 || reachRound100New) ? "1" : " 0") + "', DaCon42='" + ((DaCon42 || DaCon42New) ? "1" : " 0") + "', colorRivalery='" + ((colorRivalery || colorRivaleryNew) ? "1" : " 0") + "', longTime='" + ((longTime || longTimeNew) ? "1" : " 0") + "', timePlayed='" + (timePlayed + timePlayedNew) + "', money='" + (moneyGains + moneyGainsNew) + "' WHERE UUID='" + UUID + "';");
                                    }
                                    else {
                                        DaC.this.mysql.update("INSERT INTO " + DaC.config.tablePrefix + "PLAYERS (UUID, name, language," + "gamesPlayed, gamesWon, gamesLost, DaCdone," + "completedArena, 8playersGame, reachRound100, DaCon42, colorRivalery, longTime," + "timePlayed, money) VALUES ('" + UUID + "','" + name + "','" + language + "','" + String.valueOf(gamesPlayed) + "','" + String.valueOf(gamesWon) + "','" + String.valueOf(gamesLost) + "','" + String.valueOf(DaCdone) + "','" + (completedArena ? "1" : "0") + "','" + (eightPlayersGame ? "1" : "0") + "','" + (reachRound100 ? "1" : "0") + "','" + (DaCon42 ? "1" : "0") + "','" + (colorRivalery ? "1" : "0") + "','" + (longTime ? "1" : "0") + "','" + String.valueOf(timePlayed) + "','" + String.valueOf(moneyGains) + "');");
                                    }
                                }
                                catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        DaC.playerData.getData().set("converted", (Object)true);
                        DaC.playerData.savePlayerData();
                        if (DaC.this.arenaData.getData().contains("arenas")) {
                            for (final String name2 : DaC.this.arenaData.getData().getConfigurationSection("arenas").getKeys(false)) {
                                final ResultSet query2 = DaC.this.mysql.query("SELECT * FROM " + DaC.config.tablePrefix + "ARENAS WHERE name='" + name2 + "';");
                                try {
                                    if (query2.next()) {
                                        continue;
                                    }
                                    final ConfigurationSection sc2 = DaC.this.arenaData.getData().getConfigurationSection("arenas." + name2);
                                    final String world = sc2.getString("world");
                                    final int minAmountPlayer = sc2.getInt("minPlayer", 2);
                                    final int maxAmountPlayer = sc2.getInt("maxPlayer", 8);
                                    final Long colorIndice = sc2.getLong("colorIndice", 2122219134L);
                                    final double plateformX = sc2.getDouble("plateform.x", 0.0);
                                    final double plateformY = sc2.getDouble("plateform.y", 0.0);
                                    final double plateformZ = sc2.getDouble("plateform.z", 0.0);
                                    final double plateformYaw = sc2.getDouble("plateform.yaw", 0.0);
                                    final double plateformPitch = sc2.getDouble("plateform.pitch", 0.0);
                                    final double lobbyX = sc2.getDouble("lobby.x", 0.0);
                                    final double lobbyY = sc2.getDouble("lobby.y", 0.0);
                                    final double lobbyZ = sc2.getDouble("lobby.z", 0.0);
                                    final double lobbyYaw = sc2.getDouble("lobby.yaw", 0.0);
                                    final double lobbyPitch = sc2.getDouble("lobby.pitch", 0.0);
                                    final int minPointX = sc2.getInt("waterPool.minimum.x", 0);
                                    final int minPointY = sc2.getInt("waterPool.minimum.y", 0);
                                    final int minPointZ = sc2.getInt("waterPool.minimum.z", 0);
                                    final int maxPointX = sc2.getInt("waterPool.maximum.x", 0);
                                    final int maxPointY = sc2.getInt("waterPool.maximum.y", 0);
                                    final int maxPointZ = sc2.getInt("waterPool.maximum.z", 0);
                                    DaC.this.mysql.update("INSERT INTO " + DaC.config.tablePrefix + "ARENAS (name, world, minAmountPlayer, maxAmountPlayer, colorIndice," + "plateformX, plateformY, plateformZ, plateformYaw, plateformPitch," + "lobbyX, lobbyY, lobbyZ, lobbyYaw, lobbyPitch, minPointX," + "minPointY, minPointZ, maxPointX," + "maxPointY, maxPointZ) VALUES ('" + name2 + "','" + world + "','" + String.valueOf(minAmountPlayer) + "','" + String.valueOf(maxAmountPlayer) + "','" + String.valueOf(colorIndice) + "','" + String.valueOf(plateformX) + "','" + String.valueOf(plateformY) + "','" + String.valueOf(plateformZ) + "','" + String.valueOf(plateformYaw) + "','" + String.valueOf(plateformPitch) + "','" + String.valueOf(lobbyX) + "','" + String.valueOf(lobbyY) + "','" + String.valueOf(lobbyZ) + "','" + String.valueOf(lobbyYaw) + "','" + String.valueOf(lobbyPitch) + "','" + String.valueOf(minPointX) + "','" + String.valueOf(minPointY) + "','" + String.valueOf(minPointZ) + "','" + String.valueOf(maxPointX) + "','" + String.valueOf(maxPointY) + "','" + String.valueOf(maxPointZ) + "');");
                                }
                                catch (SQLException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                        if (DaC.this.signData.getData().contains("signs")) {
                            for (final String uuid : DaC.this.signData.getData().getConfigurationSection("signs").getKeys(false)) {
                                final ConfigurationSection sc = DaC.this.signData.getData().getConfigurationSection("signs." + uuid);
                                final String locationWorld = sc.getString("location.world");
                                final double locationX = sc.getDouble("location.X", 0.0);
                                final double locationY = sc.getDouble("location.Y", 0.0);
                                final double locationZ = sc.getDouble("location.Z", 0.0);
                                final ResultSet query3 = DaC.this.mysql.query("SELECT * FROM " + DaC.config.tablePrefix + "SIGNS WHERE (locationWorld='" + locationWorld + "' AND locationX='" + locationX + "' AND locationY='" + locationY + "' AND locationZ='" + locationZ + "') OR uuid='" + uuid + "';");
                                try {
                                    if (query3.next()) {
                                        continue;
                                    }
                                    final String type = sc.getString("type");
                                    DaC.this.mysql.update("INSERT INTO " + DaC.config.tablePrefix + "SIGNS (uuid, type ,locationWorld, locationX, locationY, locationZ) " + "VALUES ('" + uuid + "','" + type + "','" + locationWorld + "','" + locationX + "','" + locationY + "','" + locationZ + "');");
                                }
                                catch (SQLException e3) {
                                    e3.printStackTrace();
                                }
                            }
                        }
                        Bukkit.getScheduler().runTask((Plugin)DaC.plugin, (Runnable)new Runnable() {
                            @Override
                            public void run() {
                                Arena.loadArenas();
                                DacSign.loadAllSigns();
                            }
                        });
                        final Language l = DaC.playerData.getLanguageOfPlayer(player);
                        l.sendMsg(player, l.convertComplete);
                    }
                }).start();
                return true;
            }
        }
        else {
            final Arena arena = Arena.getArenaFromName(args[0]);
            if (arena == null) {
                local.sendMsg(player, local.errorArenaOrCommandNotFound);
                return true;
            }
            if (args.length == 1) {
                local.sendMsg(player, local.editErrorNoParameter);
                return true;
            }
            command = DacCommand.getCommand(args[1]);
            if (command == null) {
                local.sendMsg(player, local.errorCommandNotFound);
                return true;
            }
            if (!Permissions.hasPermission(player, command, true)) {
                return true;
            }
            final String cmdName = command.getCommandName();
            if (cmdName.equalsIgnoreCase("delete")) {
                arena.deleteArena();
                local.sendMsg(player, local.editDeleteSuccess.replace("%arenaName%", arena.getName()));
                return true;
            }
            if (cmdName.equalsIgnoreCase("setcolor")) {
                this.chooseColorGUI.openColorGUI(player, arena);
                return true;
            }
            if (cmdName.equalsIgnoreCase("setminplayer")) {
                if (args.length == 2) {
                    local.sendMsg(player, local.editLimitNoParameter);
                }
                else {
                    arena.setMinimum(player, args[2]);
                }
                return true;
            }
            if (cmdName.equalsIgnoreCase("setmaxplayer")) {
                if (args.length == 2) {
                    local.sendMsg(player, local.editLimitNoParameter);
                }
                else {
                    arena.setMaximum(player, args[2]);
                }
                return true;
            }
            if (cmdName.equalsIgnoreCase("setlobby")) {
                arena.setLobby(player);
                local.sendMsg(player, local.editLobbySuccess.replace("%arenaName%", arena.getName()));
                return true;
            }
            if (cmdName.equalsIgnoreCase("setplateform")) {
                arena.setPlateform(player);
                local.sendMsg(player, local.editPlateformSuccess.replace("%arenaName%", arena.getName()));
                return true;
            }
            if (cmdName.equalsIgnoreCase("setpool")) {
                if (arena.setPool(player)) {
                    local.sendMsg(player, local.editPoolSuccess.replace("%arenaName%", arena.getName()));
                }
                else {
                    local.sendMsg(player, local.editPoolNoSelection);
                }
                return true;
            }
            local.sendMsg(player, local.errorCommandNotFound);
            return true;
        }
    }
    
    private void changeLanguage(final Player player, final String[] args, final String cmdValue) {
        Language local = DaC.playerData.getLanguageOfPlayer(player);
        if (args.length == 1) {
            local.sendMsg(player, local.languageList);
            for (final Map.Entry<String, Language> language : Language.getLanguages().entrySet()) {
                player.sendMessage("- " + language.getValue().languageName);
            }
            return;
        }
        final Map.Entry<String, Language> entrySet = Language.getLanguage(args[1]);
        if (entrySet == null) {
            local.sendMsg(player, local.languageNotFound.replace("%cmd%", cmdValue));
            return;
        }
        DaC.playerData.setLanguage(player, entrySet.getKey());
        local = DaC.playerData.getLanguageOfPlayer(player);
        local.sendMsg(player, local.languageChangeSuccess.replace("%language%", args[1]));
    }
    
    private void displayInfo(final Player player, final String[] args, final String cmdValue) {
        final Language local = DaC.playerData.getLanguageOfPlayer(player);
        if (args.length == 1) {
            local.sendMsg(player, local.joinInfoMissingName);
            local.sendMsg(player, local.joinInfoTooltip.replace("%cmd%", cmdValue));
            return;
        }
        final Arena arena = Arena.getArenaFromName(args[1]);
        if (arena == null) {
            local.sendMsg(player, local.errorArenaNotExist.replace("%arena%", args[1]));
            local.sendMsg(player, local.joinInfoTooltip.replace("%cmd%", cmdValue));
            return;
        }
        local.sendMsg(player, local.joinInfoTooltip.replace("%cmd%", cmdValue));
        arena.displayInformation(player);
    }
    
    public void commandJoin(final DacCommand command, final Player player, final int argsLength, final String arenaName, final boolean teleport) {
        if (!Permissions.hasPermission(player, command, true)) {
            return;
        }
        final Language local = DaC.playerData.getLanguageOfPlayer(player);
        Arena arena = Arena.getArenaFromPlayer(player);
        if (arena != null) {
            local.sendMsg(player, local.errorAlreadyInGame);
            return;
        }
        if (argsLength == 1 || command.getCommandName().equalsIgnoreCase("list")) {
            this.joinGUI.openJoinGui(player, 1);
            return;
        }
        arena = Arena.getArenaFromName(arenaName);
        if (arena == null) {
            local.sendMsg(player, local.errorArenaNotExist.replace("%arena%", arenaName));
            return;
        }
        arena.addPlayerToTeam(player, teleport);
    }
    
    public void openColorGUI(final DacCommand command, final Player player) {
        if (!Permissions.hasPermission(player, command, true)) {
            return;
        }
        final Language local = DaC.playerData.getLanguageOfPlayer(player);
        final Arena arena = Arena.getArenaFromPlayer(player);
        if (arena == null) {
            local.sendMsg(player, local.errorNotInGame);
            return;
        }
        if (arena.getGameState() != GameState.READY && arena.getGameState() != GameState.STARTUP) {
            local.sendMsg(player, local.errorInGame);
            return;
        }
        ColorsGUI.openColorsGui(player, local, arena);
    }
    
    public void quitGame(final DacCommand command, final Player player) {
        if (!Permissions.hasPermission(player, command, true)) {
            return;
        }
        final Language local = DaC.playerData.getLanguageOfPlayer(player);
        final Arena arena = Arena.getArenaFromPlayer(player);
        if (arena == null) {
            local.sendMsg(player, local.errorNotInGame);
            return;
        }
        arena.removePlayerFromGame(player);
    }
    
    public void startGame(final DacCommand command, final Player player) {
        if (!Permissions.hasPermission(player, command, true)) {
            return;
        }
        final Language local = DaC.playerData.getLanguageOfPlayer(player);
        final Arena arena = Arena.getArenaFromPlayer(player);
        if (arena == null) {
            local.sendMsg(player, local.errorNotInGame);
            return;
        }
        if (arena.getGameState() == GameState.ACTIVE || arena.getGameState() == GameState.ENDING) {
            local.sendMsg(player, local.errorGameStarted);
            return;
        }
        if (arena.getGameState() == GameState.STARTUP) {
            local.sendMsg(player, local.startAlreadyStarted);
            return;
        }
        if (arena.getNonEliminated().size() < arena.getMinPlayer()) {
            local.sendMsg(player, local.startErrorQuantity.replace("%minPlayers%", String.valueOf(arena.getMinPlayer())).replace("%maxPlayers%", String.valueOf(arena.getMaxPlayer())));
            return;
        }
        if (arena.getNonEliminated().size() > arena.getMaxPlayer()) {
            local.sendMsg(player, local.startErrorQuantity.replace("%minPlayers%", String.valueOf(arena.getMinPlayer())).replace("%maxPlayers%", String.valueOf(arena.getMaxPlayer())));
            return;
        }
        if (arena.getStartTime() + 30000L > System.currentTimeMillis()) {
            local.sendMsg(player, local.startCooldown);
            return;
        }
        arena.setGameState(GameState.STARTUP);
        arena.setStartTime();
        player.closeInventory();
        arena.countdown(arena, DaC.config.countdownTime * 20);
        if (DaC.plugin.getConfiguration().broadcastStart) {
            for (final Player p : Bukkit.getOnlinePlayers()) {
                final Language localInstance = DaC.playerData.getLanguageOfPlayer(p);
                localInstance.sendMsg(p, localInstance.startBroadcast.replaceAll("%arena%", arena.getName()).replace("%time%", String.valueOf(DaC.config.countdownTime).toString()));
            }
        }
    }
    
    private void sendHelp(final Player player, final String cmdValue, final String[] args) {
        final Language local = DaC.playerData.getLanguageOfPlayer(player);
        final String header = "&8&m" + StringUtils.repeat(" ", 30) + "&r &3DeACoudre &b" + local.keyWordHelp + " &8&m" + StringUtils.repeat(" ", 30);
        if (args.length == 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', header));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3/" + cmdValue + " help general &8- " + local.helpDescriptionGeneral));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3/" + cmdValue + " help game &8- " + local.helpDescriptionGame));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3/" + cmdValue + " help arena &8- " + local.helpDescriptionArena));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3/" + cmdValue + " help admin &8- " + local.helpDescriptionAdmin));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3/" + cmdValue + " help all &8- " + local.helpDescriptionAll));
            player.sendMessage("\n");
            return;
        }
        int pageNumber = 1;
        CommandType commandType = null;
        List<DacCommand> requestedCommands;
        try {
            pageNumber = Integer.parseInt(args[1]);
            if (pageNumber < 1) {
                pageNumber = 1;
            }
            commandType = CommandType.ALL;
            requestedCommands = DacCommand.getRequiredCommands(player, commandType);
            if (pageNumber > Math.ceil(requestedCommands.size() / 3.0)) {
                pageNumber = (int)Math.ceil(requestedCommands.size() / 3.0);
            }
        }
        catch (NumberFormatException e) {
            Label_0525: {
                final String lowerCase;
                switch (lowerCase = args[1].toLowerCase()) {
                    case "general": {
                        commandType = CommandType.GENERAL;
                        break Label_0525;
                    }
                    case "game": {
                        commandType = CommandType.GAME_COMMANDS;
                        break Label_0525;
                    }
                    case "admin": {
                        commandType = CommandType.ADMIN_COMMANDS;
                        break Label_0525;
                    }
                    case "arena": {
                        commandType = CommandType.ARENA_COMMANDS;
                        break Label_0525;
                    }
                    default:
                        break;
                }
                commandType = CommandType.ALL;
            }
            requestedCommands = DacCommand.getRequiredCommands(player, commandType);
            if (args.length > 2) {
                try {
                    pageNumber = Integer.parseInt(args[2]);
                    if (pageNumber < 1) {
                        pageNumber = 1;
                    }
                    if (pageNumber > Math.ceil(requestedCommands.size() / 3.0)) {
                        pageNumber = (int)Math.ceil(requestedCommands.size() / 3.0);
                    }
                }
                catch (NumberFormatException ex) {}
            }
        }
        if (requestedCommands.size() == 0) {
            pageNumber = 0;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', header));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + local.keyWordHelpCategory + ": &7" + commandType.toString() + ", &3" + local.keyWordHelpPage + ": &7" + String.valueOf(pageNumber) + "&8/&7" + (int)Math.ceil(requestedCommands.size() / 3.0)));
        if (pageNumber == 0) {
            local.sendMsg(player, local.errorPermissionHelp);
            return;
        }
        for (int i = 3 * (pageNumber - 1); i < requestedCommands.size() && i < 3 * (pageNumber - 1) + 3; ++i) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3" + requestedCommands.get(i).getUsage().replace("%command%", cmdValue)));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &8- &7" + local.getCommandsDescription().get(requestedCommands.get(i).getDescription())));
        }
        player.sendMessage("\n");
    }
}
