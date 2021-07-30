// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.tools;

import org.bukkit.entity.Player;
import me.poutineqc.deacoudre.DeACoudre;

public class Utils
{
    public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        if (!DeACoudre.NMS_VERSION.equals("v1_8_R1")) {
            if (nmsClassString.equals("ChatSerializer")) {
                nmsClassString = "IChatBaseComponent$ChatSerializer";
            }
            if (nmsClassString.equals("EnumTitleAction")) {
                nmsClassString = "PacketPlayOutTitle$EnumTitleAction";
            }
        }
        return Class.forName("net.minecraft.server." + DeACoudre.NMS_VERSION + "." + nmsClassString);
    }
    
    private static void sendPacket(final Player p, final Object packet) {
        try {
            final Object player = p.getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(p, new Object[0]);
            final Object connection = player.getClass().getField("playerConnection").get(player);
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendTitle(final Player p, final String title, final String subtitle, final int paramInt1, final int paramInt2, final int paramInt3) {
        try {
            final Object titleEnum = getNMSClass("EnumTitleAction").getEnumConstants()[0];
            final Object subtitleEnum = getNMSClass("EnumTitleAction").getEnumConstants()[1];
            final Object timePacket = getNMSClass("PacketPlayOutTitle").getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(paramInt1, paramInt2, paramInt3);
            final Object titlePacket = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("EnumTitleAction"), getNMSClass("IChatBaseComponent")).newInstance(titleEnum, getJsonMessage(title));
            final Object subtitlePacket = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("EnumTitleAction"), getNMSClass("IChatBaseComponent")).newInstance(subtitleEnum, getJsonMessage(subtitle));
            sendPacket(p, timePacket);
            sendPacket(p, titlePacket);
            sendPacket(p, subtitlePacket);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Object getJsonMessage(final String json) throws Exception {
        return getNMSClass("ChatSerializer").getMethod("a", String.class).invoke(null, json);
    }
}
