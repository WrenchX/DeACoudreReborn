// 
// Decompiled by Procyon v0.5.36
// 

package me.poutineqc.deacoudre.tools;

import java.util.HashMap;

public class CaseInsensitiveMap extends HashMap<String, String>
{
    private static final long serialVersionUID = 2603835328506619000L;
    
    @Override
    public String put(final String key, final String value) {
        return super.put(key.toLowerCase(), value);
    }
    
    public String get(final String key) {
        return super.get(key.toLowerCase());
    }
}
