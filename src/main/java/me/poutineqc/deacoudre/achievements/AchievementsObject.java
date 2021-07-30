// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.achievements;

public class AchievementsObject
{
    private int level;
    private double reward;
    
    public AchievementsObject(final int level, final double reward) {
        this.level = level;
        this.reward = reward;
    }
    
    public int get_level() {
        return this.level;
    }
    
    public double get_reward() {
        return this.reward;
    }
}
