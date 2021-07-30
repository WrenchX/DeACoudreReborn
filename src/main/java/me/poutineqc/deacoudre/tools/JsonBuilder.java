// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.tools;

import org.bukkit.ChatColor;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class JsonBuilder
{
    List<JsonElement> messages;
    
    public JsonBuilder() {
        this.messages = new ArrayList<JsonElement>();
    }
    
    public void add(final JsonElement element) {
        this.messages.add(element);
    }
    
    public void clear() {
        this.messages.clear();
    }
    
    public List<JsonElement> getMessages() {
        return this.messages;
    }
    
    public String getJson() {
        final StringBuilder json = new StringBuilder();
        for (final JsonElement message : this.messages) {
            if (json.length() == 0) {
                json.append('[');
            }
            else {
                json.append(',');
            }
            json.append(message.generate());
        }
        json.append(']');
        return json.toString();
    }
    
    public static String getJson(final JsonElement element) {
        final JsonBuilder builder = new JsonBuilder();
        builder.add(element);
        return builder.getJson();
    }
    
    public static String getEmpty() {
        final JsonBuilder builder = new JsonBuilder();
        builder.add(new JsonElement(""));
        return builder.getJson();
    }
    
    public static class JsonElement
    {
        String message;
        boolean bold;
        boolean italic;
        boolean underlined;
        boolean strikethrough;
        boolean obfuscated;
        ChatColor color;
        
        public JsonElement(final String message, final ChatColor color, final boolean bold, final boolean italic, final boolean underlined, final boolean strikethrough, final boolean obfuscated) {
            this.message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
            this.color = color;
            this.bold = bold;
            this.italic = italic;
            this.underlined = underlined;
            this.strikethrough = strikethrough;
            this.obfuscated = obfuscated;
        }
        
        public JsonElement(final String message) {
            this.message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
            this.bold = false;
            this.italic = false;
            this.underlined = false;
            this.strikethrough = false;
            this.obfuscated = false;
        }
        
        public JsonElement(final String message, final ChatColor color) {
            this.message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
            this.color = color;
            this.bold = false;
            this.italic = false;
            this.underlined = false;
            this.strikethrough = false;
            this.obfuscated = false;
        }
        
        public void setMessage(final String message) {
            this.message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
        }
        
        public void setColor(final ChatColor color) {
            this.color = color;
        }
        
        public void setBold(final boolean bold) {
            this.bold = bold;
        }
        
        public void setItalic(final boolean italic) {
            this.italic = italic;
        }
        
        public void setUnderlined(final boolean underlined) {
            this.underlined = underlined;
        }
        
        public void setStrikethrough(final boolean strikethrough) {
            this.strikethrough = strikethrough;
        }
        
        public void setObfuscated(final boolean obfuscated) {
            this.obfuscated = obfuscated;
        }
        
        public String generate() {
            final StringBuilder json = new StringBuilder();
            json.append('{');
            json.append("\"text\":\"" + this.message + "\"");
            if (this.color != null) {
                json.append(",\"color\":\"" + this.color.name().toLowerCase() + "\"");
            }
            if (this.bold) {
                json.append(",\"bold\":true");
            }
            if (this.italic) {
                json.append(",\"italic\":true");
            }
            if (this.underlined) {
                json.append(",\"underlined\":true");
            }
            if (this.strikethrough) {
                json.append(",\"strikethrough\":true");
            }
            if (this.obfuscated) {
                json.append(",\"obfuscated\":true");
            }
            json.append('}');
            return json.toString();
        }
    }
}
