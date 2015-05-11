package com.upgov.core.utils;

import java.util.Dictionary;

/**
 * Created by intelligrape on 2/22/2015.
 */
public class DictionaryUtils {


    private static final String RAWTYPES = "rawtypes";


    private DictionaryUtils() {
    }


    public static String getString(
            @SuppressWarnings(RAWTYPES) final Dictionary properties,
            final String key) {
        if (null != properties && null != properties.get(key)) {
            return (String) properties.get(key);
        }
        return null;
    }


    public static String[] getStringArray(
            @SuppressWarnings(RAWTYPES) final Dictionary properties,
            final String key) {
        if (null != properties && null != properties.get(key)) {
            return (String[]) properties.get(key);
        }
        return null;
    }


    public static long getLong(
            @SuppressWarnings(RAWTYPES) final Dictionary properties,
            final String key) {
        if (null != properties && null != properties.get(key)) {
            return ((Long) properties.get(key)).longValue();
        }
        return 0L;
    }


    public static boolean getBoolean(
            @SuppressWarnings(RAWTYPES) final Dictionary properties,
            final String key) {
        if (null != properties && null != properties.get(key)) {
            return ((Boolean) properties.get(key)).booleanValue();
        }
        return false;
    }
}
