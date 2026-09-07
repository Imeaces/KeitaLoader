package org.imeaces.keitaload;

import org.tinylog.Logger;

public class KeitaConfigurations {
    public static final boolean STANDALONE_MODS_CLASSLOADER =
            getBool("keitaload.standalone-mods-classloader", true);

    public static final boolean LOAD_CLASSPATH_MODS =
            getBool("keitaload.load-classpath-mods", false);

    public static boolean getBool(String propKey, boolean defaultValue) {
        String value = System.getProperty(propKey);
        if (value == null) {
            return defaultValue;
        }
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        }

        Logger.warn("invalid bool property: {} = {}, using default value {}", propKey, value, defaultValue);
        return defaultValue;
    }
}
