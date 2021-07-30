// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.commands;

public enum CommandType
{
    GENERAL("GENERAL", 0), 
    GAME_COMMANDS("GAME_COMMANDS", 1), 
    ARENA_COMMANDS("ARENA_COMMANDS", 2), 
    ADMIN_COMMANDS("ADMIN_COMMANDS", 3), 
    ALL("ALL", 4);
    
    private CommandType(final String name, final int ordinal) {
    }
}
