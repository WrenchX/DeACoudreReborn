// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.commands;

public enum SignType
{
    JOIN("JOIN", 0), 
    PLAY("PLAY", 1), 
    START("START", 2), 
    QUIT("QUIT", 3), 
    COLOR("COLOR", 4), 
    STATS("STATS", 5);
    
    private SignType(final String name, final int ordinal) {
    }
}
