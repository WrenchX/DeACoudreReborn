// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.commands;

import me.poutineqc.deacoudre.instances.GameState;
import me.poutineqc.deacoudre.Language;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.block.BlockState;
import me.poutineqc.deacoudre.instances.Arena;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import me.poutineqc.deacoudre.DeACoudre;
import java.util.ArrayList;
import org.bukkit.Location;
import java.util.UUID;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import me.poutineqc.deacoudre.PlayerData;
import me.poutineqc.deacoudre.MySQL;
import me.poutineqc.deacoudre.Configuration;

public class DacSign
{
    private static Configuration config;
    private static MySQL mysql;
    private static PlayerData playerData;
    private static File signFile;
    private static YamlConfiguration signData;
    private static List<DacSign> signs;
    private UUID uuid;
    private SignType type;
    private Location location;
    
    static {
        DacSign.signs = new ArrayList<DacSign>();
    }
    
    public DacSign(final DeACoudre plugin) {
        DacSign.config = plugin.getConfiguration();
        DacSign.mysql = plugin.getMySQL();
        DacSign.playerData = plugin.getPlayerData();
        DacSign.signFile = new File(plugin.getDataFolder(), "signData.yml");
        if (!DacSign.signFile.exists()) {
            try {
                DacSign.signFile.createNewFile();
            }
            catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create signData.ylm.");
            }
        }
        loadSignData();
    }
    
    private static void loadSignData() {
        DacSign.signData = YamlConfiguration.loadConfiguration(DacSign.signFile);
    }
    
    public static void loadAllSigns() {
        DacSign.signs.clear();
        if (DacSign.mysql.hasConnection()) {
            final ResultSet query = DacSign.mysql.query("SELECT * FROM " + DacSign.config.tablePrefix + "SIGNS;");
            try {
                while (query.next()) {
                    final UUID uuid = UUID.fromString(query.getString("uuid"));
                    final SignType type = getSignType(query.getString("type"));
                    final Location location = new Location(Bukkit.getWorld(query.getString("locationWorld")), (double)query.getInt("locationX"), (double)query.getInt("locationY"), (double)query.getInt("locationZ"));
                    new DacSign(uuid, location, type);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            if (!DacSign.signData.contains("signs")) {
                return;
            }
            for (final String uuid2 : DacSign.signData.getConfigurationSection("signs").getKeys(false)) {
                final ConfigurationSection cs = DacSign.signData.getConfigurationSection("signs." + uuid2);
                final SignType type2 = getSignType(cs.getString("type", UUID.randomUUID().toString()));
                final Location location2 = new Location(Bukkit.getWorld(cs.getString("location.world")), (double)cs.getInt("location.X", 0), (double)cs.getInt("location.Y", 0), (double)cs.getInt("location.Z"));
                new DacSign(UUID.fromString(uuid2), location2, type2);
            }
        }
        updateSigns();
    }
    
    private static SignType getSignType(final String string) {
        switch (string) {
            case "JOIN": {
                return SignType.JOIN;
            }
            case "PLAY": {
                return SignType.PLAY;
            }
            case "QUIT": {
                return SignType.QUIT;
            }
            case "COLOR": {
                return SignType.COLOR;
            }
            case "START": {
                return SignType.START;
            }
            case "STATS": {
                return SignType.STATS;
            }
            default:
                break;
        }
        return null;
    }
    
    public DacSign(final UUID uuid, final Location location, final SignType type) {
        this.uuid = uuid;
        this.type = type;
        this.location = location;
        boolean delete = false;
        if (type == null) {
            delete = true;
        }
        Arena arena = null;
        BlockState block = null;
        try {
            block = location.getBlock().getState();
            if (!(block instanceof Sign)) {
                delete = true;
            }
            else {
                final Sign sign = (Sign)block;
                arena = Arena.getArenaFromName(sign.getLine(2));
                if (arena == null) {
                    if (type == SignType.JOIN || type == SignType.PLAY) {
                        delete = true;
                    }
                }
                else if (type == SignType.PLAY && arena.getWorld() != location.getWorld()) {
                    delete = true;
                }
            }
        }
        catch (NullPointerException e) {
            delete = true;
        }
        if (delete) {
            this.removeSign();
            return;
        }
        DacSign.signs.add(this);
        if (type == SignType.JOIN || type == SignType.PLAY) {
            updateSigns(arena);
        }
    }
    
    public DacSign(final SignChangeEvent event, final SignType type) {
        final Language local = DacSign.playerData.getLanguage(DacSign.config.language);
        final Arena arena = Arena.getArenaFromName(event.getLine(2));
        this.uuid = UUID.randomUUID();
        this.type = type;
        this.location = event.getBlock().getLocation();
        switch (type) {
            case COLOR: {
                event.setLine(0, "");
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                event.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signColor));
                event.setLine(3, "");
                break;
            }
            case JOIN: {
                event.setLine(0, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', local.signJoin));
                switch (arena.getGameState()) {
                    case ACTIVE:
                    case ENDING: {
                        event.setLine(3, ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateActive));
                        break;
                    }
                    case READY:
                    case STARTUP: {
                        event.setLine(3, String.valueOf(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordScoreboardPlayers))) + " : " + String.valueOf(arena.getNonEliminated().size()) + "/" + String.valueOf(arena.getMaxPlayer()));
                        break;
                    }
                    case UNREADY: {
                        event.setLine(3, ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateUnset));
                        break;
                    }
                }
                break;
            }
            case PLAY: {
                event.setLine(0, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', local.signPlay));
                switch (arena.getGameState()) {
                    case ACTIVE:
                    case ENDING: {
                        event.setLine(3, ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateActive));
                        break;
                    }
                    case READY:
                    case STARTUP: {
                        event.setLine(3, String.valueOf(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordScoreboardPlayers))) + " : " + String.valueOf(arena.getNonEliminated().size()) + "/" + String.valueOf(arena.getMaxPlayer()));
                        break;
                    }
                    case UNREADY: {
                        event.setLine(3, ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateUnset));
                        break;
                    }
                }
                break;
            }
            case QUIT: {
                event.setLine(0, "");
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                event.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signQuit));
                event.setLine(3, "");
                break;
            }
            case START: {
                event.setLine(0, "");
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                event.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signStart));
                event.setLine(3, "");
                break;
            }
            case STATS: {
                event.setLine(0, "");
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                event.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signStats));
                event.setLine(3, "");
                break;
            }
            default: {
                event.setLine(0, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', local.signNotValid1));
                event.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signNotValid2));
                event.setLine(3, ChatColor.translateAlternateColorCodes('&', local.signNotValid3));
                break;
            }
        }
        DacSign.signs.add(this);
        if (type == SignType.JOIN || type == SignType.PLAY) {
            updateSigns(arena);
        }
        if (DacSign.mysql.hasConnection()) {
            DacSign.mysql.update("INSERT INTO " + DacSign.config.tablePrefix + "SIGNS (uuid, type ,locationWorld, locationX, locationY, locationZ) " + "VALUES ('" + this.uuid + "','" + type + "','" + this.location.getWorld().getName() + "','" + this.location.getBlockX() + "','" + this.location.getBlockY() + "','" + this.location.getBlockZ() + "');");
        }
        else {
            DacSign.signData.set("signs." + this.uuid.toString() + ".type", (Object)type.toString());
            DacSign.signData.set("signs." + this.uuid.toString() + ".location.world", (Object)this.location.getWorld().getName());
            DacSign.signData.set("signs." + this.uuid.toString() + ".location.X", (Object)this.location.getBlockX());
            DacSign.signData.set("signs." + this.uuid.toString() + ".location.Y", (Object)this.location.getBlockY());
            DacSign.signData.set("signs." + this.uuid.toString() + ".location.Z", (Object)this.location.getBlockZ());
            saveSignData();
        }
    }
    
    public static void arenaDelete(final Arena arena) {
        for (final DacSign dacsign : DacSign.signs) {
            if (!arena.getName().equalsIgnoreCase(((Sign)dacsign.location.getBlock().getState()).getLine(2))) {
                continue;
            }
            removeSign(dacsign);
            arenaDelete(arena);
        }
    }
    
    public static void removeSign(final DacSign dacsign) {
        dacsign.removeSign();
    }
    
    public void removeSign() {
        if (this.location.getBlock().getState() instanceof Sign) {
            final Sign sign = (Sign)this.location.getBlock().getState();
            sign.setLine(0, " ");
            sign.setLine(1, " ");
            sign.setLine(2, " ");
            sign.setLine(3, " ");
            sign.getLocation().getChunk().load();
            sign.update();
        }
        DacSign.signs.remove(this);
        if (DacSign.mysql.hasConnection()) {
            DacSign.mysql.update("DELETE FROM " + DacSign.config.tablePrefix + "SIGNS WHERE uuid='" + this.uuid.toString() + "';");
        }
        else {
            DacSign.signData.set("signs." + this.uuid.toString(), (Object)null);
            saveSignData();
        }
    }
    
    public static void updateSigns() {
        final Language local = DacSign.playerData.getLanguage(DacSign.config.language);
        for (final DacSign dacsign : DacSign.signs) {
            final Sign sign = (Sign)dacsign.location.getBlock().getState();
            switch (dacsign.type) {
                case COLOR: {
                    sign.setLine(1, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signColor));
                    break;
                }
                case JOIN: {
                    sign.setLine(0, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                    sign.setLine(1, ChatColor.translateAlternateColorCodes('&', local.signJoin));
                    break;
                }
                case PLAY: {
                    sign.setLine(0, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                    sign.setLine(1, ChatColor.translateAlternateColorCodes('&', local.signPlay));
                    break;
                }
                case QUIT: {
                    sign.setLine(1, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signQuit));
                    break;
                }
                case START: {
                    sign.setLine(1, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signStart));
                    break;
                }
                case STATS: {
                    sign.setLine(1, ChatColor.translateAlternateColorCodes('&', local.prefixLong));
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', local.signStats));
                    break;
                }
            }
            sign.update();
            if (dacsign.type != SignType.JOIN && dacsign.type != SignType.PLAY) {
                continue;
            }
            final Arena arena = Arena.getArenaFromName(sign.getLine(2));
            if (arena == null) {
                continue;
            }
            dacsign.updateGameState(sign, arena);
        }
    }
    
    public static void updateSigns(final Arena arena) {
        for (final DacSign dacsign : DacSign.signs) {
            if (arena == null) {
                continue;
            }
            final Sign sign = (Sign)dacsign.location.getBlock().getState();
            if (!sign.getLine(2).equalsIgnoreCase(arena.getName())) {
                continue;
            }
            dacsign.updateGameState(sign, arena);
        }
    }
    
    private void updateGameState(final Sign sign, final Arena arena) {
        final Language local = DacSign.playerData.getLanguage(DacSign.config.language);
        switch (arena.getGameState()) {
            case ACTIVE:
            case ENDING: {
                sign.setLine(3, ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateActive));
                break;
            }
            case READY:
            case STARTUP: {
                sign.setLine(3, String.valueOf(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', local.keyWordScoreboardPlayers))) + " : " + String.valueOf(arena.getNonEliminated().size()) + "/" + String.valueOf(arena.getMaxPlayer()));
                break;
            }
            case UNREADY: {
                sign.setLine(3, ChatColor.translateAlternateColorCodes('&', local.keyWordGameStateUnset));
                break;
            }
        }
        sign.update();
    }
    
    public static void removePlaySigns(final Arena arena) {
        for (final DacSign dacsign : DacSign.signs) {
            if (arena.getName().equalsIgnoreCase(((Sign)dacsign.location.getBlock().getState()).getLine(2))) {
                if (dacsign.type != SignType.PLAY) {
                    continue;
                }
                removeSign(dacsign);
                DacSign.signs.remove(dacsign);
                removePlaySigns(arena);
            }
        }
    }
    
    public static DacSign getDacSign(final Location location) {
        for (final DacSign dacsign : DacSign.signs) {
            if (dacsign.location.getWorld() == location.getWorld() && dacsign.location.distance(location) == 0.0) {
                return dacsign;
            }
        }
        return null;
    }
    
    public SignType getSignType() {
        return this.type;
    }
    
    private static void saveSignData() {
        try {
            DacSign.signData.save(DacSign.signFile);
        }
        catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save signData.yml!");
        }
    }
    
    public YamlConfiguration getData() {
        return DacSign.signData;
    }
}
