// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre;

import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.poutineqc.deacoudre.instances.User;
import java.util.Iterator;
import me.poutineqc.deacoudre.commands.DacCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.InputStream;
import me.poutineqc.deacoudre.tools.CaseInsensitiveMap;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;

public class Language
{
    private static DeACoudre plugin;
    private static Configuration config;
    private static File langFolder;
    private File languageFile;
    private FileConfiguration languageData;
    private static HashMap<String, Language> languages;
    public String keyWordGeneralAnd;
    public String keyWordGeneralComma;
    public String languageName;
    String errorNoPermission;
    public String errorAlreadyInGame;
    public String errorNotInGame;
    public String errorGameStarted;
    public String startAlreadyStarted;
    public String startStopped;
    public String joinStateUnset;
    public String joinStateFull;
    public String joinStateStarted;
    public String joinAsSpectator;
    public String joinGamePlayer;
    public String joinGameOthers;
    public String quitGamePlayer;
    public String quitGameOthers;
    public String gameTimeOutPlayer;
    public String gameTimeOutOthers;
    public String startErrorQuantity;
    public String startBroadcast;
    public String startRandomColor;
    public String startRandomOrder;
    public String startPosition;
    public String gameTurnPlayer;
    public String gameTurnOthers;
    public String endingBroadcastSingle;
    public String endingStall;
    public String endingBroadcastMultiple;
    public String gameNewRound;
    public String gamePointsUpPlayer;
    public String gamePointsUpOthers;
    public String gamePointsFlushPlayer;
    public String gamePointsFlushOthers;
    public String gamePointsRevivePlayer;
    public String gamePointsReviveOthers;
    public String gamePointsEliminatePlayer;
    public String gamePointsEliminateOthers;
    public String gamePointsDownPlayer;
    public String gamePointsDownOthers;
    public String gamePointsReviveLastLastPlayer;
    public String gamePointsReviveLastLastOthers;
    public String gamePointsReviveLastMultiplePlayer;
    public String gamePointsReviveLastMultipleOthers;
    public String gamePointsConfirmationPlayer;
    public String gamePointsConfirmationOthers;
    public String gameSuccessPlayer;
    public String gameSuccessOthers;
    public String gamePointsReviveHint;
    public String endingRewardMoney;
    public String challengeRewardMoney;
    public String challengeBroadcast;
    public String challengeDisplayPlayed;
    public String challengeDisplayWin;
    public String challengeDisplayLost;
    public String challengeDisplayDaC;
    public String challengeDisplayCompleteArena;
    public String challengeDisplay8PlayersGame;
    public String challengeDisplayReachRound100;
    public String challengeDisplayAnswerToLife;
    public String challengeDisplayFight;
    public String challengeDisplayMinecraftSnail;
    public String keyWordStats;
    public String keyWordChallenges;
    public String keyWordStatsReward;
    public String keyWordStatsGamesPlayed;
    public String keyWordStatsGamesWon;
    public String keyWordStatsGamesLost;
    public String keyWordStatsDacsDone;
    public String keyWordStatsProgression;
    public String keyWordStatsTimePlayed;
    public String keyWordStatsMoneyGot;
    public String keyWordGeneralHours;
    public String keyWordGeneralMinutes;
    public String keyWordGeneralSeconds;
    public String keyWordJumpFast;
    public String keyWordStatsCompleted;
    public String keyWordStatsNotCompleted;
    public String keyWordStatsTop10;
    public String keyWordGuiPreviousPage;
    public String keyWordGuiNextPage;
    public String startCooldown;
    public String errorInGame;
    public String joinGuiTitle;
    public String keyWordGameStateUnset;
    public String keyWordGameStateStarted;
    public String keyWordGameStateFull;
    public String keyWordGameStateReady;
    public String colorGuiTitle;
    public String colorGuiCurrent;
    public String errorArenaNotExist;
    public String colorChoosen;
    public String colorRandom;
    public String colorAlreadyPicked;
    public String signJoin;
    public String signQuit;
    public String signColor;
    public String signStart;
    public String signStats;
    public String signPlay;
    public String signNotValid1;
    public String signNotValid2;
    public String signNotValid3;
    public String signNoPermission0;
    public String signNoPermission1;
    public String signNoPermission2;
    public String signNoPermission3;
    public String editNewNoName;
    public String editNewSuccess;
    public String editNewExists;
    public String editNewLong;
    public String editErrorNoParameter;
    public String editDeleteSuccess;
    public String editLobbySuccess;
    public String editPlateformSuccess;
    public String editPoolNoSelection;
    public String editPoolSuccess;
    public String editLimitMinSuccess;
    public String editLimitMaxSuccess;
    public String editLimitNaN;
    public String editLimitMinBelowMin;
    public String editLimitErrorMinMax;
    public String editLimitNoParameter;
    public String keyWordColorRandom;
    public String errorCommandNotFound;
    public String errorArenaOrCommandNotFound;
    public String reloadSucess;
    public String keyWordColorWhite;
    public String keyWordColorOrange;
    public String keyWordColorMagenta;
    public String keyWordColorLightBlue;
    public String keyWordColorYellow;
    public String keyWordColorLime;
    public String keyWordColorPink;
    public String keyWordColorGrey;
    public String keyWordColorLightGrey;
    public String keyWordColorCyan;
    public String keyWordColorPurple;
    public String keyWordColorBlue;
    public String keyWordColorBrown;
    public String keyWordColorGreen;
    public String keyWordColorRed;
    public String keyWordColorBlack;
    public String languageList;
    public String forcestartError;
    public String endingRewardItemsSpaceMultiple;
    public String endingRewardItemsSpaceOne;
    public String endingRewardItemsReceive;
    public String joinGuiTooltip;
    private CaseInsensitiveMap commandDescriptions;
    public String editColorGuiTitle;
    public String keyWordGuiInstrictions;
    public String editColorGuiTooltip;
    public String editColorActive;
    public String pluginDevelopper;
    public String pluginVersion;
    public String pluginHelp;
    public String keyWordHelp;
    public String helpDescriptionGeneral;
    public String helpDescriptionGame;
    public String helpDescriptionArena;
    public String helpDescriptionAdmin;
    public String helpDescriptionAll;
    public String keyWordHelpCategory;
    public String errorPermissionHelp;
    public String keyWordHelpPage;
    public String languageNotFound;
    public String languageChangeSuccess;
    public String joinInfoMissingName;
    public String joinInfoTooltip;
    public String keyWordHelpInformation;
    public String keyWordHelpCurrent;
    public String keyWordGameState;
    public String keyWordHelpAmountPlayer;
    public String keyWordGeneralMinimum;
    public String keyWordGeneralMaximum;
    public String keyWordGameStateActive;
    public String keyWordGameStateStartup;
    public String keyWordHelpAdvanced;
    public String keyWordHelpLobby;
    public String keyWordHelpWorld;
    public String keyWordHelpPlateform;
    public String keyWordHelpPool;
    public String editColorColorLessPlayer;
    public String endingTeleport;
    public String keyWordColorClay;
    public String keyWordColorWool;
    public String endingSimulation;
    public String errorTeleport;
    public String editLimitMaxAboveMax;
    public String keyWordScoreboardPlayers;
    public String keyWordScoreboardPoints;
    public String joinNewPlacePlayer;
    public String joinNewPlaceOthers;
    public String prefixLong;
    public String prefixShort;
    public String editLimitGameActive;
    public String editColorNoPool;
    public String editErrorNoArena;
    public String editColorChoosen;
    public String keyWordScoreboardRound;
    public String startAutoFail;
    public String convertAlreadyDone;
    public String convertStart;
    public String convertNoMysql;
    public String convertComplete;
    
    static {
        Language.languages = new HashMap<String, Language>();
    }
    
    Language(final DeACoudre plugin) {
        Language.plugin = plugin;
        Language.config = plugin.getConfiguration();
        Language.langFolder = new File(plugin.getDataFolder(), "LanguageFiles");
        if (!Language.langFolder.exists()) {
            Language.langFolder.mkdir();
        }
    }
    
    Language(final String fileName, final boolean forceFileOverwrite) {
        this.languageFile = new File(Language.langFolder.getPath(), String.valueOf(fileName) + ".yml");
        if (forceFileOverwrite) {
            this.languageFile.delete();
            Language.plugin.saveResource("LanguageFiles/" + fileName + ".yml", false);
        }
        if (!this.languageFile.exists()) {
            final InputStream local = Language.plugin.getResource("LanguageFiles/" + fileName + ".yml");
            if (local != null) {
                Language.plugin.saveResource("LanguageFiles/" + fileName + ".yml", false);
            }
            else {
                Language.plugin.getLogger().info("Could not find " + fileName + ".yml");
            }
        }
        Language.languages.put(fileName, this);
        this.loadLang();
    }
    
    public void loadLang() {
        this.languageData = (FileConfiguration)YamlConfiguration.loadConfiguration(this.languageFile);
        this.languageName = this.languageData.getString("languageName", "english");
        this.prefixLong = this.languageData.getString("prefixLong", "&1[&3DeACoudre&1]");
        this.prefixShort = this.languageData.getString("prefixShort", "&1[&3DaC&1] ");
        this.pluginDevelopper = this.languageData.getString("pluginDevelopper", "&3Developped by: &7%developper%");
        this.pluginVersion = this.languageData.getString("pluginVersion", "&3Version: &7%version%");
        this.pluginHelp = this.languageData.getString("pluginHelp", "&3Type &b/%command% help &3 for the list of commands.");
        this.errorNoPermission = this.languageData.getString("errorNoPermission", "&cYou don't have the permission to do that!");
        this.errorPermissionHelp = this.languageData.getString("errorPermissionHelp", "&cYou do not have any permissions in this category.");
        this.errorArenaNotExist = this.languageData.getString("errorArenaNotExist", "&cNot a valid arena name.");
        this.errorCommandNotFound = this.languageData.getString("errorCommandNotFound", "&eCommand does not exist &b(&3/dac help &bfor help)&e.");
        this.errorArenaOrCommandNotFound = this.languageData.getString("errorArenaOrCommandNotFound", "&eCommand or arena not found &b(&3/dac help &bfor help)&e.");
        this.errorInGame = this.languageData.getString("errorInGame", "&cYou can't do that during a game.");
        this.errorNotInGame = this.languageData.getString("errorNotInGame", "&cYou are not in a game at the moment.");
        this.errorAlreadyInGame = this.languageData.getString("errorAlreadyInGame", "&cYou are already in a game. Do &d/dac quit &cto quit it.");
        this.errorGameStarted = this.languageData.getString("errorGameStarted", "&cThe game is already started.");
        this.errorTeleport = this.languageData.getString("errorTeleport", "&cYou can't teleport away while in a DeACoudre game.");
        this.helpDescriptionAll = this.languageData.getString("helpDescriptionAll", "&7All Commands");
        this.helpDescriptionGeneral = this.languageData.getString("helpDescriptionGeneral", "&7General player commands");
        this.helpDescriptionGame = this.languageData.getString("helpDescriptionGame", "&7Commands to simply play the game");
        this.helpDescriptionArena = this.languageData.getString("helpDescriptionArena", "&7Commands to setup the arenas");
        this.helpDescriptionAdmin = this.languageData.getString("helpDescriptionAdmin", "&7Admin maintenance commands");
        this.languageList = this.languageData.getString("languageList", "&3Available languages:");
        this.languageNotFound = this.languageData.getString("languageNotFound", "&cLanguage not found. &8/%cmd% language &cfor a list of available languages");
        this.languageChangeSuccess = this.languageData.getString("languageChangeSuccess", "&aLanguage successfully set to %language%");
        this.joinGuiTitle = this.languageData.getString("joinGuiTitle", "&2Arena List &0: &3DeACoudre");
        this.joinGuiTooltip = this.languageData.getString("joinGuiTooltip", "&7Click on the arena\n&7you wish to join\n&7Right click to display\n&7it's infos");
        this.joinStateUnset = this.languageData.getString("joinStateUnset", "&cThis arena is not ready to use. Ask an admin to finish setting it up.");
        this.joinStateFull = this.languageData.getString("joinStateFull", "&cThe arena is full.");
        this.joinStateStarted = this.languageData.getString("joinStateStarted", "&cThe game has already started.");
        this.joinAsSpectator = this.languageData.getString("joinAsSpectator", "&bJoining the lobby as a spectator.");
        this.joinGamePlayer = this.languageData.getString("joinGamePlayer", "&aJoined the lobby from the arena &2%arenaName% &a(&2%amountInGame%&a)");
        this.joinGameOthers = this.languageData.getString("joinGameOthers", "&f%player% &3just joined the DaC lobby &a(&2%amountInGame%&a).");
        this.joinNewPlaceOthers = this.languageData.getString("joinNewPlaceOthers", "&f%player% &3is added to the game to replace &f%leaver%&3.");
        this.joinNewPlacePlayer = this.languageData.getString("joinNewPlacePlayer", "&3You are added to the game to replace &f%leaver%&3!");
        this.joinInfoMissingName = this.languageData.getString("joinInfoMissingName", "&cYou need to choose an arena.");
        this.joinInfoTooltip = this.languageData.getString("joinInfoTooltip", "&8[&7Tip&8] &7You may also do &8/%cmd% list &7and right click an arena to display it's information.");
        this.quitGamePlayer = this.languageData.getString("quitGamePlayer", "&aYou left the DaC game.");
        this.quitGameOthers = this.languageData.getString("quitGameOthers", "&f%player% &3left the DaC game.");
        this.colorGuiTitle = this.languageData.getString("colorGuiTitle", "&6Choose Color &0: &3DeACoudre");
        this.colorGuiCurrent = this.languageData.getString("colorGuiCurrent", "Current Color:");
        this.colorChoosen = this.languageData.getString("colorChoosen", "&dYou have choosen the &f%material% &d: &f%color%&d.");
        this.colorRandom = this.languageData.getString("colorRandom", "&dYou let the fate decide of your color.");
        this.colorAlreadyPicked = this.languageData.getString("colorAlreadyPicked", "&cThis color has been picked while you were choosing. Sorry, try again.");
        this.startRandomColor = this.languageData.getString("startRandomColor", "&5You were randomly assigned the &f%material% &5: &f%color%&5.");
        this.startRandomOrder = this.languageData.getString("startRandomOrder", "&9Random Position Order List:");
        this.startPosition = this.languageData.getString("startPosition", "&9%posNo% - &f%player%");
        this.startAlreadyStarted = this.languageData.getString("startAlreadyStarted", "&cThe countdown has already been started.");
        this.startStopped = this.languageData.getString("startStopped", "&cThere is not enough players to start a game anymore. The countdown has been stopped.");
        this.startErrorQuantity = this.languageData.getString("startErrorQuantity", "&cThere must be between &4%minPlayers% &cand &4%maxPlayers% &cplayers to start the game.");
        this.startCooldown = this.languageData.getString("startCooldown", "&cYou can't start a game so fast after the last was aborted. Wait 30 seconds.");
        this.startAutoFail = this.languageData.getString("startAutoFail", "&cThe counter did not autostart because the last one was aborted less than 30 seconds ago.");
        this.startBroadcast = this.languageData.getString("startBroadcast", "&6A new game of DaC will start in %time% seconds in the arena &3%arena%&6. All interested players may do &4/dac join %arena% &6to join the game.");
        this.gameNewRound = this.languageData.getString("gameNewRound", "&8Round %round% has started");
        this.gameTurnPlayer = this.languageData.getString("gameTurnPlayer", "&d&lIt's your turn to play!");
        this.gameTurnOthers = this.languageData.getString("gameTurnOthers", "&dIt's &f%player%&d's turn to play!");
        this.gameSuccessPlayer = this.languageData.getString("gameSuccessPlayer", "&dYou successfully jumped");
        this.gameSuccessOthers = this.languageData.getString("gameSuccessOthers", "&f%player% &dsuccessfully jumped");
        this.gamePointsUpPlayer = this.languageData.getString("gamePointsUpPlayer", "&6Congratuation! &dYou just did a DaC! (&5%points%&d)");
        this.gamePointsUpOthers = this.languageData.getString("gamePointsUpOthers", "&f%player% &djust did a DaC! (&5%points%&d)");
        this.gamePointsDownPlayer = this.languageData.getString("gamePointsDownPlayer", "&eYou lost a life (&6%points%&e)");
        this.gamePointsDownOthers = this.languageData.getString("gamePointsDownOthers", "&f%player% &ehas lost a life (&6%points%&e)");
        this.gamePointsConfirmationPlayer = this.languageData.getString("gamePointsConfirmationPlayer", "&dOh dear, you don't have any life left. Waiting for confirmation...");
        this.gamePointsConfirmationOthers = this.languageData.getString("gamePointsConfirmationOthers", "&f%player% &dfailed and has no points left. Waiting for confirmation...");
        this.gamePointsReviveHint = this.languageData.getString("gamePointsReviveHint", "&8(&7You may get it back if everybody else fails and someone losses his final life this round&8)");
        this.gamePointsReviveLastLastPlayer = this.languageData.getString("gamePointsReviveLastLastPlayer", "&dYou lost you last life, but you are given another chance since everybody also failed this round.");
        this.gamePointsReviveLastLastOthers = this.languageData.getString("gamePointsReviveLastLastOthers", "&f%player% &dhas lost his last life but is given another chance since everybody else did fail this round..");
        this.gamePointsReviveLastMultiplePlayer = this.languageData.getString("gamePointsReviveLastMultiplePlayer", "&dYou failed.. as did everyone else this round and someone lost his last life. Your are given another chance");
        this.gamePointsReviveLastMultipleOthers = this.languageData.getString("gamePointsReviveLastMultipleOthers", "&f%player% &dhas failed but is given another chance since everybody else did fail this round and someone lost his last life..");
        this.gamePointsRevivePlayer = this.languageData.getString("gamePointsRevivePlayer", "&dYou are also receiving another chance. (&5%points%&d)");
        this.gamePointsReviveOthers = this.languageData.getString("gamePointsReviveOthers", "&f%player% &dhas been given another chance. (&5%points%&d)");
        this.gamePointsEliminatePlayer = this.languageData.getString("gamePointsEliminatePlayer", "&cOh dear, you don't have any life left. You are now eliminated.");
        this.gamePointsEliminateOthers = this.languageData.getString("gamePointsEliminateOthers", "&f%player% &chas been eliminated.");
        this.gamePointsFlushPlayer = this.languageData.getString("gamePointsFlushPlayer", "&f%player%&c's success means your loss. You are eliminated.");
        this.gamePointsFlushOthers = this.languageData.getString("gamePointsFlushOthers", "&cDue to &f%player%&c's success, &f%looser% &cis now eliminated.");
        this.gameTimeOutPlayer = this.languageData.getString("gameTimeOutPlayer", "&cIt took you too long to play. You are now eliminated.");
        this.gameTimeOutOthers = this.languageData.getString("gameTimeOutOthers", "&f%player% &ctook too long to play. He is now eliminated.");
        this.endingBroadcastSingle = this.languageData.getString("endingBroadcastSingle", "&6Congratulation to &f%player% &6who just won a game of DaC in the arena &3%arenaName%&6.");
        this.endingStall = this.languageData.getString("endingStall", "&eThe game got stale since no successful move has been made for &6%time% &emoves.\n&6Calculating who will be the winner... &7(The one with the most lives)");
        this.endingBroadcastMultiple = this.languageData.getString("endingBroadcastMultiple", "&6Congratulation to &f%players% &6who just finished completely the arena &3%arenaName%&6.");
        this.endingRewardMoney = this.languageData.getString("endingRewardMoney", "&dYou receive &5%currency%%amount% &dfor your win.");
        this.endingRewardItemsSpaceMultiple = this.languageData.getString("endingRewardItemsSpaceMultiple", "&cYou don't have place in you inventory for all your rewards.");
        this.endingRewardItemsSpaceOne = this.languageData.getString("endingRewardItemsSpaceOne", "&cYou don't have place in you inventory for your reward.");
        this.endingRewardItemsReceive = this.languageData.getString("endingRewardItemsReceive", "&aYou win %amount% &f%item% &afor your victory.");
        this.endingTeleport = this.languageData.getString("endingTeleport", "&dGame is over. Teleporting back in 5 seconds...");
        this.endingSimulation = this.languageData.getString("endingSimulation", "&6The simulation is over!");
        this.challengeDisplayPlayed = this.languageData.getString("challengeDisplayPlayed", "Play %amount% game(s)");
        this.challengeDisplayWin = this.languageData.getString("challengeDisplayWin", "Win %amount% game(s)");
        this.challengeDisplayLost = this.languageData.getString("challengeDisplayLost", "Loose %amount% game(s)");
        this.challengeDisplayDaC = this.languageData.getString("challengeDisplayDaC", "Achieve %amount% DaC(s)");
        this.challengeDisplayCompleteArena = this.languageData.getString("challengeDisplayCompleteArena", "Complete an Arena");
        this.challengeDisplay8PlayersGame = this.languageData.getString("challengeDisplay8PlayersGame", "Play a 8 players game");
        this.challengeDisplayReachRound100 = this.languageData.getString("challengeDisplayReachRound100", "Reach round 100");
        this.challengeDisplayAnswerToLife = this.languageData.getString("challengeDisplayAnswerToLife", "The answer to life the universe and everything");
        this.challengeDisplayFight = this.languageData.getString("challengeDisplayFight", "Fight!");
        this.challengeDisplayMinecraftSnail = this.languageData.getString("challengeDisplayMinecraftSnail", "The Minecraft snail");
        this.challengeRewardMoney = this.languageData.getString("challengeRewardMoney", "&dYou receive &5%currency%%amount% &dfor the completion of your challenge.");
        this.challengeBroadcast = this.languageData.getString("challengeBroadcast", "&f%player% &6just achieved the challenge: &4%challenge%");
        this.signJoin = this.languageData.getString("signJoin", "&aJoin Arena");
        this.signPlay = this.languageData.getString("signPlay", "&bPlay");
        this.signQuit = this.languageData.getString("signQuit", "&cQuit Arena");
        this.signColor = this.languageData.getString("signColor", "&6Change Color");
        this.signStart = this.languageData.getString("signStart", "&9Start Game");
        this.signStats = this.languageData.getString("signStats", "&5Stats");
        this.signNotValid1 = this.languageData.getString("signNotValid1", "&cNone valid");
        this.signNotValid2 = this.languageData.getString("signNotValid2", "&csign parameters");
        this.signNotValid3 = this.languageData.getString("signNotValid3", "&cTry again");
        this.signNoPermission0 = this.languageData.getString("signNoPermission0", "&cYou don't have");
        this.signNoPermission1 = this.languageData.getString("signNoPermission1", "&cthe permissions");
        this.signNoPermission2 = this.languageData.getString("signNoPermission2", "&cto create a DaC");
        this.signNoPermission3 = this.languageData.getString("signNoPermission3", "&csign, &4Sorry...");
        this.editErrorNoArena = this.languageData.getString("editErrorNoArena", "&cYou must provide an arena name for this command.");
        this.editErrorNoParameter = this.languageData.getString("editErrorNoParameter", "&eYou must choose what you want to do with this arena.");
        this.editNewNoName = this.languageData.getString("editNewNoName", "&cYou must provide a name for the new arena.");
        this.editNewExists = this.languageData.getString("editNewExists", "&cAn arena named &4%arenaName% &calready exists.");
        this.editNewLong = this.languageData.getString("editNewLong", "&cThe arena's name can't be more than one word.");
        this.editNewSuccess = this.languageData.getString("editNewSuccess", "&aNew arena &2%arenaName% &asuccessfully created.");
        this.editDeleteSuccess = this.languageData.getString("editDeleteSuccess", "&aSuccessfully deleted the arena &2%arenaName%");
        this.editLobbySuccess = this.languageData.getString("editLobbySuccess", "&aLobby sucessfully set for the arena &2%arenaName%&a.");
        this.editPlateformSuccess = this.languageData.getString("editPlateformSuccess", "&aPlateform sucessfully set for the arena &2%arenaName%&a.");
        this.editPoolNoSelection = this.languageData.getString("editPoolNoSelection", "&cYou must first make a selection with world edit.");
        this.editPoolSuccess = this.languageData.getString("editPoolSuccess", "&aPool sucessfully set for the arena &2%arenaName%&a.");
        this.editLimitMinSuccess = this.languageData.getString("editLimitMinSuccess", "&aSuccessfully set to &2%amount% &athe minimum amount of players for the arena &2%arenaName%");
        this.editLimitMaxSuccess = this.languageData.getString("editLimitMaxSuccess", "&aSuccessfully set to &2%amount% &athe maximum amount of players for the arena &2%arenaName%");
        this.editLimitGameActive = this.languageData.getString("editLimitGameActive", "&cYou can't edit the amount of player while there is a game active");
        this.editLimitNaN = this.languageData.getString("editLimitNaN", "&cThe amount must be a natural number.");
        this.editLimitNoParameter = this.languageData.getString("editLimitNoParameter", "&cYou must provide a number.");
        this.editLimitMinBelowMin = this.languageData.getString("editLimitMinBelowMin", "&cThe min amount can't be below 2");
        this.editLimitMaxAboveMax = this.languageData.getString("editLimitMaxAboveMax", "&cThe max amount can't be above 12.");
        this.editLimitErrorMinMax = this.languageData.getString("editLimitErrorMinMax", "&cThe max can't be above the min (and vice-versa)");
        this.editColorGuiTitle = this.languageData.getString("editColorGuiTitle", "&eEdit Colors &0: &3DeACoudre");
        this.editColorGuiTooltip = this.languageData.getString("editColorGuiTooltip", "&eThe enchanted blocks are\n&ethe curently selected ones.\n&eClick a block to\n&eenable or disable it.");
        this.editColorColorLessPlayer = this.languageData.getString("editColorColorLessPlayer", "&cCan't have less available colors than max players.");
        this.editColorNoPool = this.languageData.getString("editColorNoPool", "&cYou can't edit the colors before the pool has been defined.");
        this.editColorChoosen = this.languageData.getString("editColorChoosen", "&cYou can't remove this block right now. It has already been choosen by a player.");
        this.editColorActive = this.languageData.getString("editColorActive", "&cYou can't edit the colors while a game is active.");
        this.reloadSucess = this.languageData.getString("reloadSucess", "&aDaC has been successfully reloaded.");
        this.forcestartError = this.languageData.getString("forcestartError", "&cMust have only one player in a game to forcestart it.");
        this.convertAlreadyDone = this.languageData.getString("convertAlreadyDone", "&cThe conversion to mysql has already been done.");
        this.convertStart = this.languageData.getString("convertStart", "&aBegining the conversion. This may take a very long time..");
        this.convertNoMysql = this.languageData.getString("convertNoMysql", "&cYou must have a mysql connection to do this command.");
        this.convertComplete = this.languageData.getString("convertComplete", "&aThe file to mysql conversion is finished!");
        this.commandDescriptions = new CaseInsensitiveMap();
        for (final DacCommand cmd : DacCommand.getCommands()) {
            this.commandDescriptions.put(cmd.getDescription(), this.languageData.getString(cmd.getDescription(), "&cOops, an Error has occured!"));
        }
        this.keyWordGeneralAnd = this.languageData.getString("keyWordGeneralAnd", " &6and &f");
        this.keyWordGeneralComma = this.languageData.getString("keyWordGeneralComma", "&6, &f");
        this.keyWordGeneralMinimum = this.languageData.getString("keyWordGeneralMinimum", "Minimum");
        this.keyWordGeneralMaximum = this.languageData.getString("keyWordGeneralMaximum", "Maximum");
        this.keyWordGeneralHours = this.languageData.getString("keyWordGeneralHours", "hours");
        this.keyWordGeneralMinutes = this.languageData.getString("keyWordGeneralMinutes", "minutes");
        this.keyWordGeneralSeconds = this.languageData.getString("keyWordGeneralSeconds", "seconds");
        this.keyWordColorWool = this.languageData.getString("keyWordColorWool", "Wool");
        this.keyWordColorClay = this.languageData.getString("keyWordColorClay", "Clay");
        this.keyWordColorWhite = this.languageData.getString("keyWordColorWhite", "&fWhite");
        this.keyWordColorOrange = this.languageData.getString("keyWordColorOrange", "&6Orange");
        this.keyWordColorMagenta = this.languageData.getString("keyWordColorMagenta", "&dMagenta");
        this.keyWordColorLightBlue = this.languageData.getString("keyWordColorLightBlue", "&9Light Blue");
        this.keyWordColorYellow = this.languageData.getString("keyWordColorYellow", "&eYellow");
        this.keyWordColorLime = this.languageData.getString("keyWordColorLime", "&aLime");
        this.keyWordColorPink = this.languageData.getString("keyWordColorPink", "&dPink");
        this.keyWordColorGrey = this.languageData.getString("keyWordColorGrey", "&8Grey");
        this.keyWordColorLightGrey = this.languageData.getString("keyWordColorLightGrey", "&7Light Grey");
        this.keyWordColorCyan = this.languageData.getString("keyWordColorCyan", "&bCyan");
        this.keyWordColorPurple = this.languageData.getString("keyWordColorPurple", "&5Purple");
        this.keyWordColorBlue = this.languageData.getString("keyWordColorBlue", "&1Blue");
        this.keyWordColorBrown = this.languageData.getString("keyWordColorBrown", "&fBrown");
        this.keyWordColorGreen = this.languageData.getString("keyWordColorGreen", "&2Green");
        this.keyWordColorRed = this.languageData.getString("keyWordColorRed", "&4Red");
        this.keyWordColorBlack = this.languageData.getString("keyWordColorBlack", "&fBlack");
        this.keyWordColorRandom = this.languageData.getString("keyWordColorRandom", "&6R&da&2n&9d&co&3m");
        this.keyWordGameState = this.languageData.getString("keyWordGameState", "game state");
        this.keyWordGameStateStarted = this.languageData.getString("keyWordGameStateStarted", "&cAlready Started");
        this.keyWordGameStateFull = this.languageData.getString("keyWordGameStateFull", "&cArena Full");
        this.keyWordGameStateReady = this.languageData.getString("keyWordGameStateReady", "&aReady");
        this.keyWordGameStateUnset = this.languageData.getString("keyWordGameStateUnset", "&7Arena Unset");
        this.keyWordGameStateStartup = this.languageData.getString("keyWordGameStateStartup", "&9Startup");
        this.keyWordGameStateActive = this.languageData.getString("keyWordGameStateActive", "&cActive");
        this.keyWordChallenges = this.languageData.getString("keyWordChallenges", "&dChallenges");
        this.keyWordStats = this.languageData.getString("keyWordStats", "&5Stats");
        this.keyWordStatsTop10 = this.languageData.getString("keyWordStatsTop10", "Top 10");
        this.keyWordStatsReward = this.languageData.getString("keyWordStatsReward", "Reward");
        this.keyWordStatsGamesPlayed = this.languageData.getString("keyWordStatsGamesPlayed", "Games Played");
        this.keyWordStatsGamesWon = this.languageData.getString("keyWordStatsGamesWon", "Games Won");
        this.keyWordStatsGamesLost = this.languageData.getString("keyWordStatsGamesLost", "Games Lost");
        this.keyWordStatsDacsDone = this.languageData.getString("keyWordStatsDacsDone", "DaCs Achieved");
        this.keyWordStatsTimePlayed = this.languageData.getString("keyWordStatsTimePlayed", "Time Played");
        this.keyWordStatsMoneyGot = this.languageData.getString("keyWordStatsMoneyGot", "Money Got");
        this.keyWordStatsProgression = this.languageData.getString("keyWordStatsProgression", "Progression");
        this.keyWordStatsCompleted = this.languageData.getString("keyWordStatsCompleted", "Completed");
        this.keyWordStatsNotCompleted = this.languageData.getString("keyWordStatsNotCompleted", "Not Completed");
        this.keyWordHelp = this.languageData.getString("keyWordHelp", "Help");
        this.keyWordHelpCategory = this.languageData.getString("keyWordHelpCategory", "Category");
        this.keyWordHelpPage = this.languageData.getString("keyWordHelpPage", "Page");
        this.keyWordHelpAdvanced = this.languageData.getString("keyWordHelpAdvanced", "Advanced Information");
        this.keyWordHelpInformation = this.languageData.getString("keyWordHelpInformation", "Information");
        this.keyWordHelpCurrent = this.languageData.getString("keyWordHelpCurrent", "Current");
        this.keyWordHelpAmountPlayer = this.languageData.getString("keyWordHelpAmountPlayer", "amount of players");
        this.keyWordHelpWorld = this.languageData.getString("keyWordHelpWorld", "World");
        this.keyWordHelpLobby = this.languageData.getString("keyWordHelpLobby", "Lobby");
        this.keyWordHelpPlateform = this.languageData.getString("keyWordHelpPlateform", "Plateform");
        this.keyWordHelpPool = this.languageData.getString("keyWordHelpPool", "Pool");
        this.keyWordScoreboardPlayers = this.languageData.getString("keyWordScoreboardPlayers", "&6Players");
        this.keyWordScoreboardPoints = this.languageData.getString("keyWordScoreboardPoints", "&6Points");
        this.keyWordScoreboardRound = this.languageData.getString("keyWordScoreboardRound", "Round");
        this.keyWordJumpFast = this.languageData.getString("keyWordJumpFast", "Jump!");
        this.keyWordGuiPreviousPage = this.languageData.getString("keyWordGuiPreviousPage", "&dPrevious Page");
        this.keyWordGuiNextPage = this.languageData.getString("keyWordGuiNextPage", "&dNext Page");
        this.keyWordGuiInstrictions = this.languageData.getString("keyWordGuiInstrictions", "&6Instructions");
    }
    
    public void sendMsg(final User user, final String msg) {
        this.sendMsg(user.getPlayer(), msg);
    }
    
    public void sendMsg(final Player player, final String msg) {
        if (Language.config.introInFrontOfEveryMessage) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefixShort) + msg.toString()));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg.toString()));
        }
    }
    
    public static Map.Entry<String, Language> getLanguage(final String languageName) {
        for (final Map.Entry<String, Language> local : Language.languages.entrySet()) {
            if (local.getValue().languageName.equalsIgnoreCase(languageName)) {
                return local;
            }
        }
        return null;
    }
    
    public static HashMap<String, Language> getLanguages() {
        return Language.languages;
    }
    
    static void clearLanguages() {
        Language.languages.clear();
    }
    
    public CaseInsensitiveMap getCommandsDescription() {
        return this.commandDescriptions;
    }
}
