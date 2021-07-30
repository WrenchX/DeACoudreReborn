// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.achievements;

import java.util.Iterator;
import me.poutineqc.deacoudre.DeACoudre;
import java.util.ArrayList;
import java.util.List;
import me.poutineqc.deacoudre.PlayerData;

public class TopManager
{
    private PlayerData playerData;
    private String player;
    private double score;
    private static List<TopManager> games;
    private static List<TopManager> won;
    private static List<TopManager> lost;
    private static List<TopManager> dacdone;
    
    static {
        TopManager.games = new ArrayList<TopManager>();
        TopManager.won = new ArrayList<TopManager>();
        TopManager.lost = new ArrayList<TopManager>();
        TopManager.dacdone = new ArrayList<TopManager>();
    }
    
    public TopManager(final DeACoudre plugin) {
        this.playerData = plugin.getPlayerData();
        this.updateAll();
    }
    
    public TopManager(final String name, final double score) {
        this.player = name;
        this.score = score;
    }
    
    public void updateAll() {
        TopManager.games = this.updateTop("gamesPlayed");
        TopManager.won = this.updateTop("gamesWon");
        TopManager.lost = this.updateTop("gamesLost");
        TopManager.dacdone = this.updateTop("DaCdone");
    }
    
    private List<TopManager> updateTop(final String lookup) {
        final List<TopManager> tempList = new ArrayList<TopManager>();
        if (this.playerData.getData().contains("players")) {
            for (final String uuid : this.playerData.getData().getConfigurationSection("players").getKeys(false)) {
                final String name = this.playerData.getData().getString("players." + uuid + ".name", "unknown");
                final double score = this.playerData.getData().getDouble("players." + uuid + "." + lookup, 0.0);
                tempList.add(0, new TopManager(name, score));
                for (int i = 0; i < 10 && i < tempList.size() - 1; ++i) {
                    if (tempList.get(i).score < tempList.get(i + 1).score) {
                        final TopManager tempValue = tempList.get(i);
                        tempList.set(i, tempList.get(i + 1));
                        tempList.set(i + 1, tempValue);
                    }
                }
            }
        }
        return tempList;
    }
    
    protected String getPlayer() {
        return this.player;
    }
    
    protected double getScore() {
        return this.score;
    }
    
    public static List<TopManager> getGames() {
        return TopManager.games;
    }
    
    public static List<TopManager> getWon() {
        return TopManager.won;
    }
    
    public static List<TopManager> getLost() {
        return TopManager.lost;
    }
    
    public static List<TopManager> getDaCdone() {
        return TopManager.dacdone;
    }
}
