// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.commands;

import me.poutineqc.deacoudre.Permissions;
import java.util.List;
import org.bukkit.entity.Player;
import java.util.Iterator;
import java.io.InputStream;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import me.poutineqc.deacoudre.DeACoudre;
import java.util.ArrayList;

public class DacCommand
{
    //TODO: Plugin.YML
    private String commandName;
    private String description;
    private String permission;
    private String usage;
    private CommandType type;
    private static ArrayList<DacCommand> commands;
    private static DeACoudre plugin;
    private static File commandFile;
    private static FileConfiguration commandData;
    
    public DacCommand(final String commandName, final String description, final String permission, final String usage, final CommandType type) {
        this.commandName = commandName;
        this.description = description;
        this.permission = permission;
        this.usage = usage;
        this.type = type;
    }
    
    public DacCommand(final DeACoudre plugin) {
        DacCommand.plugin = plugin;
        DacCommand.commandFile = new File(plugin.getDataFolder(), "commands.yml");
        loadCommands();
    }
    
    private static void loadCommands() {
        final InputStream local = DacCommand.plugin.getResource("commands.yml");
        if (local != null) {
            DacCommand.plugin.saveResource("commands.yml", false);
        }
        else {
            DacCommand.plugin.getLogger().severe("Could not find commands.yml inside the jar file.");
        }
        DacCommand.commandData = (FileConfiguration)YamlConfiguration.loadConfiguration(DacCommand.commandFile);
        DacCommand.commands = new ArrayList<DacCommand>();
        readingProcess();
        DacCommand.commandFile.delete();
    }
    
    private static void readingProcess() {
        for (final String commandType : DacCommand.commandData.getConfigurationSection("commands").getKeys(false)) {
            CommandType type = null;
            Label_0160: {
                final String s;
                switch (s = commandType) {
                    case "general": {
                        type = CommandType.GENERAL;
                        break Label_0160;
                    }
                    case "game": {
                        type = CommandType.GAME_COMMANDS;
                        break Label_0160;
                    }
                    case "admin": {
                        type = CommandType.ADMIN_COMMANDS;
                        break Label_0160;
                    }
                    case "arena": {
                        type = CommandType.ARENA_COMMANDS;
                        break Label_0160;
                    }
                    default:
                        break;
                }
                type = CommandType.ALL;
            }
            for (final String commandName : DacCommand.commandData.getConfigurationSection("commands." + commandType).getKeys(false)) {
                final String description = DacCommand.commandData.getString("commands." + commandType + "." + commandName + ".description");
                final String permission = DacCommand.commandData.getString("commands." + commandType + "." + commandName + ".permission");
                final String usage = DacCommand.commandData.getString("commands." + commandType + "." + commandName + ".usage");
                DacCommand.commands.add(new DacCommand(commandName, description, permission, usage, type));
            }
        }
    }
    
    public static ArrayList<DacCommand> getCommands() {
        return DacCommand.commands;
    }
    
    public static List<DacCommand> getRequiredCommands(final Player player, final CommandType commandType) {
        final List<DacCommand> requestedCommands = new ArrayList<DacCommand>();
        for (final DacCommand cmd : DacCommand.commands) {
            if ((cmd.type == commandType || commandType == CommandType.ALL) && Permissions.hasPermission(player, cmd.permission, false)) {
                requestedCommands.add(cmd);
            }
        }
        return requestedCommands;
    }
    
    public static DacCommand getCommand(final String argument) {
        for (final DacCommand command : DacCommand.commands) {
            if (command.commandName.equalsIgnoreCase(argument)) {
                return command;
            }
        }
        return null;
    }
    
    public String getCommandName() {
        return this.commandName;
    }
    
    public String getPermission() {
        return this.permission;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getUsage() {
        return this.usage;
    }
}
