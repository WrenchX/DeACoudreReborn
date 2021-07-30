// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.instances;

public enum GameState
{
    UNREADY("UNREADY", 0), 
    READY("READY", 1), 
    STARTUP("STARTUP", 2), 
    ACTIVE("ACTIVE", 3), 
    ENDING("ENDING", 4);
    
    private GameState(final String name, final int ordinal) {
    }
}
