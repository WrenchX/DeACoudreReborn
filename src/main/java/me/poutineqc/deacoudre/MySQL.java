// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;

public class MySQL
{
    private DeACoudre plugin;
    private Configuration config;
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private Connection connection;
    
    public MySQL() {
        this.connection = null;
    }
    
    public MySQL(final DeACoudre plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.host = this.config.host;
        this.port = this.config.port;
        this.database = this.config.database;
        this.user = this.config.user;
        this.password = this.config.password;
        this.connect();
    }
    
    public void updateInfo(final DeACoudre plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.host = this.config.host;
        this.port = this.config.port;
        this.database = this.config.database;
        this.user = this.config.user;
        this.password = this.config.password;
        this.connect();
    }
    
    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.user, this.password);
        }
        catch (SQLException e) {
            this.plugin.getLogger().info("[MySQL] The connection to MySQL couldn't be made! reason: " + e.getMessage());
        }
    }
    
    public void close() {
        try {
            if (this.connection != null) {
                this.connection.close();
                this.plugin.getLogger().info("[MySQL] The connection to MySQL is ended successfully!");
            }
        }
        catch (SQLException e) {
            this.plugin.getLogger().info("[MySQL] The connection couldn't be closed! reason: " + e.getMessage());
        }
    }
    
    public void update(final String qry) {
        try {
            final PreparedStatement st = this.connection.prepareStatement(qry);
            st.execute();
            st.close();
        }
        catch (SQLException e) {
            this.connect();
            System.err.println(e);
        }
    }
    
    public boolean hasConnection() {
        return this.connection != null;
    }
    
    public ResultSet query(final String qry) {
        ResultSet rs = null;
        try {
            final PreparedStatement st = this.connection.prepareStatement(qry);
            rs = st.executeQuery();
        }
        catch (SQLException e) {
            this.connect();
            System.err.println(e);
        }
        return rs;
    }
}
